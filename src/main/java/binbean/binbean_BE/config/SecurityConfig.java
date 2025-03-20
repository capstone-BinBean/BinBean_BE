package binbean.binbean_BE.config;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.filter.JwtExceptionFilter;
import binbean.binbean_BE.auth.filter.JwtUsernamePasswordAuthFilter;
import binbean.binbean_BE.auth.filter.JwtVerificationFilter;
import binbean.binbean_BE.infra.RedisService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtVerificationFilter jwtVerificationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final RedisService redisService;

    // AuthenticationManager의 Bean을 얻기 위한 authConfiguration 객체
    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(JwtVerificationFilter jwtVerificationFilter, JwtExceptionFilter jwtExceptionFilter, AuthenticationConfiguration authenticationConfiguration, RedisService redisService) {
        this.jwtVerificationFilter = jwtVerificationFilter;
        this.jwtExceptionFilter = jwtExceptionFilter;
        this.redisService = redisService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    /**
     * AuthenticationConfiguration로부터 AuthenticationManager 객체 가져오는 메서드
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        // UsernameNotFoundException을 BadCredentialsException으로 감싸지 않도록 설정
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {

        // 커스텀 필터 등록 : 로그인 경로 설정 후, 로그인 필터 등록
        JwtUsernamePasswordAuthFilter filter = new JwtUsernamePasswordAuthFilter(authenticationManager(), jwtTokenProvider, redisService);
        filter.setFilterProcessesUrl("/api/auths/login");

        http
            .cors(Customizer.withDefaults())
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests((requests) ->
                requests
                    .requestMatchers(HttpMethod.POST, "/api/auths/registration", "/api/auths/login")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtVerificationFilter, JwtUsernamePasswordAuthFilter.class)
            .addFilterBefore(jwtExceptionFilter, JwtVerificationFilter.class)
            .httpBasic(HttpBasicConfigurer::disable)
            .formLogin(FormLoginConfigurer::disable);

        return http.build();
    }
}
