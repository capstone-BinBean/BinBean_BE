package binbean.binbean_BE.service.auth;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.constants.Constants.Kakao;
import binbean.binbean_BE.dto.auth.KakaoAccount;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.dto.auth.response.KakaoResponse;
import binbean.binbean_BE.exception.user.UserAlreadyExistException;
import binbean.binbean_BE.entity.user.User;
import binbean.binbean_BE.repository.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(RestTemplate restTemplate, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * accessToken 검증
     */
    public boolean validateKakaoAccessToken(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(JwtTokenProvider.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // FIXME
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> jsonResponse = restTemplate.exchange(Kakao.KAKAO_TOKEN_INFO_URL,
                HttpMethod.GET, entity, String.class);
//            ObjectMapper objectMapper = new ObjectMapper();
//            var response = objectMapper.readValue(jsonResponse.getBody(), KakaoResponse.class);


            logger.info("", jsonResponse.getStatusCode());
            logger.info("", jsonResponse.getBody());

            getKakaoUserInfo(accessToken);

            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public void getKakaoUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(JwtTokenProvider.AUTHORIZATION, JwtTokenProvider.BEARER_PREFIX + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // FIXME
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> jsonResponse = restTemplate.exchange(Kakao.KAKAO_USER_INFO_URL,
                HttpMethod.GET, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            var response = objectMapper.readValue(jsonResponse.getBody(), KakaoResponse.class);

            logger.info("headers: ", response.kakaoAcount().getEmail());
            logger.info("body: ", jsonResponse.getBody());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User getUserEntity(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
