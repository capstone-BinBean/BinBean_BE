package binbean.binbean_BE.config;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.filter.JwtExceptionFilter;
import binbean.binbean_BE.auth.filter.JwtUsernamePasswordAuthFilter;
import binbean.binbean_BE.auth.filter.JwtVerificationFilter;
import binbean.binbean_BE.auth.filter.UrlBasedAuthenticationFilter;
import binbean.binbean_BE.constants.Constants.URL;
import binbean.binbean_BE.encryption.AESUtils;
import binbean.binbean_BE.infra.RedisService;
import binbean.binbean_BE.service.AuthService;
import java.util.List;
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
    private final AuthService authService;
    private final RedisService redisService;
    private final AESUtils aesUtils;

    // AuthenticationManager의 Bean을 얻기 위한 authConfiguration 객체
    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(JwtVerificationFilter jwtVerificationFilter,
        JwtExceptionFilter jwtExceptionFilter, AuthService authService,
        AuthenticationConfiguration authenticationConfiguration,
        RedisService redisService, AESUtils aesUtils) {
        this.jwtVerificationFilter = jwtVerificationFilter;
        this.jwtExceptionFilter = jwtExceptionFilter;
        this.authService = authService;
        this.redisService = redisService;
        this.authenticationConfiguration = authenticationConfiguration;
        this.aesUtils = aesUtils;
    }

    /**
     * AuthenticationConfiguration로부터 AuthenticationManager 객체 가져오는 메서드
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtUsernamePasswordAuthFilter jwtUsernamePasswordAuthFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, RedisService redisService)
        throws Exception {
        var filter = new JwtUsernamePasswordAuthFilter(authenticationManager, authService, jwtTokenProvider, redisService, aesUtils);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {
        // 일반 로그인 필터 (일반 로그인 경로에만 적용)
        JwtUsernamePasswordAuthFilter loginFilter = new JwtUsernamePasswordAuthFilter(authenticationManager(), authService, jwtTokenProvider, redisService, aesUtils);
        loginFilter.setFilterProcessesUrl(URL.NORMAL_LOGIN_URL);

        // 카카오 로그인 필터 (카카오 로그인 경로에만 적용)
        JwtUsernamePasswordAuthFilter socialLoginFilter = new JwtUsernamePasswordAuthFilter(authenticationManager(), authService, jwtTokenProvider, redisService, aesUtils);
        socialLoginFilter.setFilterProcessesUrl(URL.KAKAO_LOGIN_URL);

        // OncePerRequestFilter 등록하여 경로에 따라 필터를 분기 처리
        UrlBasedAuthenticationFilter filter = new UrlBasedAuthenticationFilter(loginFilter, socialLoginFilter);

        http
            .cors(Customizer.withDefaults())
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests((requests) ->
                requests
                    .requestMatchers(HttpMethod.POST, URL.ALLOWED_URLS).permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/admin/**").permitAll() // -> 관리자 계정만 접속 할 수 있도록 변경
                    .anyRequest()
                    .authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(jwtVerificationFilter, JwtUsernamePasswordAuthFilter.class)
            .addFilterBefore(jwtExceptionFilter, JwtVerificationFilter.class)
            .httpBasic(HttpBasicConfigurer::disable)
            .formLogin(FormLoginConfigurer::disable);

        return http.build();
    }
}
