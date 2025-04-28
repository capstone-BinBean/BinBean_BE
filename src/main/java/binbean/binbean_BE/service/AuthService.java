package binbean.binbean_BE.service;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.constants.Constants.ErrorMsg;
import binbean.binbean_BE.dto.auth.TokenDto;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
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

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
        RedisService redisService, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
        this.jwtTokenProvider = jwtTokenProvider;
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
     * 액세스 토큰 만료 시 회원 검증 후, 리프레쉬 토큰을 검증해서 액세스 토큰과 리프레쉬 토큰을 재발급
     */
    public TokenDto reissue(String refreshToken) {
        // refreshToken 유효성, 만료 검사
        jwtTokenProvider.validateRefreshToken(refreshToken);
        String username = jwtTokenProvider.getUsername(refreshToken);

        String refreshTokenInRedis = redisService.getValues(username)
            .orElseThrow(UnauthorizedException::new);

        // redis에 저장된 토큰과 같은지를 비교 (같지 않으면 삭제 및 재로그인 요청)
        if (!jwtTokenProvider.isRefreshTokenMatched(refreshToken, refreshTokenInRedis)) {
            redisService.deleteValues(username);
            throw new UnauthorizedException();
        }

        // UserDetails 불러와서 토큰 재발급
        UserDetailsImpl userDetails = (UserDetailsImpl) loadUserByUsername(username);
        // 액세스 토큰 재발급 및 redis 업데이트
        redisService.deleteValues(username);
        var tokenDto = jwtTokenProvider.generateToken(userDetails);
        // redis에 refresh token 저장
        redisService.setStringValue(userDetails.getUsername(), tokenDto.getRefreshToken(),
            jwtTokenProvider.getRefreshExpirationTime());
        return tokenDto;
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
