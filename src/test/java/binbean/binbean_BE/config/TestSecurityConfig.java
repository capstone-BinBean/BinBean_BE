package binbean.binbean_BE.config;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.encryption.AESUtils;
import binbean.binbean_BE.infra.RedisService;
import binbean.binbean_BE.repository.UserRepository;
import binbean.binbean_BE.service.AuthService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//@TestConfiguration
//public class TestSecurityConfig {
//    // 실제 UserDetailsService 구현 (AuthService 빈)
//    @Bean
//    public AuthService userDetailsService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
//        RedisService redisService, JwtTokenProvider jwtTokenProvider, AESUtils aesUtils) {
//        return new AuthService(userRepository, passwordEncoder, redisService,
//            jwtTokenProvider, aesUtils);
//    }
//}