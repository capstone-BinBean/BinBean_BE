package binbean.binbean_BE.service;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.constants.Constants.ErrorMsg;
import binbean.binbean_BE.dto.auth.TokenDto;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.encryption.AESUtils;
import binbean.binbean_BE.exception.UnauthorizedException;
import binbean.binbean_BE.exception.UserAlreadyExistException;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.infra.RedisService;
import binbean.binbean_BE.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AESUtils aesUtils;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
        RedisService redisService, JwtTokenProvider jwtTokenProvider, AESUtils aesUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.aesUtils = aesUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = getUserEntity(username);
        return new UserDetailsImpl(user);
    }

    public void registerUser(RegisterRequest request) {
        userRepository.findByEmail(request.email())
            .ifPresent( user -> {
                throw new UserAlreadyExistException(user.getEmail());
            });

        userRepository.findByNickname(request.nickname())
            .ifPresent(user -> {
                throw new UserAlreadyExistException(user.getNickname());
            });

        var user = request.toEntity();
        user.setPassword(encodePassword(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * 액세스 토큰 만료 시 회원 검증 후, 전달받은 암호화된 리프레쉬 토큰을 복호화/검증해서 액세스 토큰과 리프레쉬 토큰을 재발급
     */
    public TokenDto reissue(String encryptedRefreshToken) {
        // refreshToken 복호화
        String refreshToken = aesUtils.decryptWithAesKey(encryptedRefreshToken);

        // refreshToken 유효성, 만료 검사
        jwtTokenProvider.validateToken(refreshToken);
        String username = jwtTokenProvider.getUsername(refreshToken);

        String refreshTokenInRedis = aesUtils.decryptWithAesKey(redisService.getValues(username)
            .orElseThrow(UnauthorizedException::new));

        // redis에 저장된 토큰과 같은지를 비교 (같지 않으면 삭제 및 재로그인 요청)
        if (!jwtTokenProvider.validateRefreshToken(refreshToken, refreshTokenInRedis)) {
            redisService.deleteValues(username);
            throw new UnauthorizedException();
        }

        // UserDetails 불러와서 토큰 재발급
        UserDetailsImpl userDetails = (UserDetailsImpl) loadUserByUsername(username);
        // 액세스 토큰 재발급 및 redis 업데이트
        redisService.deleteValues(username);
        var tokenDto = jwtTokenProvider.generateToken(userDetails);
        // 새로 갱신된 refresh token 암호화
        tokenDto.setEncryptedRefreshToken(aesUtils.encryptWithAesKey(refreshToken));

        // redis에 refresh token 저장
        redisService.setStringValue(userDetails.getUsername(), tokenDto.getRefreshToken(),
            jwtTokenProvider.getRefreshExpirationTime());
        return tokenDto;
    }

    /**
     * 사용자가 로그아웃한 후에도, Access Token을 다시 사용해서 요청을 보낼 가능성이 있음
     * 그리하여 Redis에서 "logout" 값이 있으면, 해당 토큰은 더 이상 사용할 수 없도록 체크
     * 로그아웃 후 기존 Access Token이 유효해도 사용 불가 (로그아웃된 토큰 차단)
     * 키 : accessToken, 값: "logout"
     */
    public void logout(String accessToken, String encryptedRefreshToken) {
        // 액세스 토큰 유효성 검사
        jwtTokenProvider.validateToken(accessToken);
        String username = jwtTokenProvider.getUsername(accessToken);

        // refresh token 복호화 및 redis에서 조회
        String refreshToken = aesUtils.decryptWithAesKey(encryptedRefreshToken);
        String refreshTokenInRedis = aesUtils.decryptWithAesKey(redisService.getValues(username)
            .orElseThrow(UnauthorizedException::new));

        // 요청받은 refreshToken과 레디스에 저장된 refreshToken이 동일한지 추가 검증
        if (!refreshToken.equals(refreshTokenInRedis)) {
            throw new UnauthorizedException();
        }

        // redis에 저장되어있는 refreshToken 삭제 (로그아웃 후 더 이상 재발급 요청 불가)
        redisService.deleteValues(username);

        // 사용자가 로그아웃했음을 기록하기 위해 Access Token을 Redis에 저장
        long expTime = jwtTokenProvider.getRemainingValidityTime(accessToken);
        redisService.setStringValue(accessToken, "logout", expTime);
    }

    /**
     * 소셜 로그인일 경우, password를 null로 보냄
     */
    private String encodePassword(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        return passwordEncoder.encode(password);
    }

    private User getUserEntity(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
