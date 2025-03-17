package binbean.binbean_BE.config;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.filter.JwtUsernamePasswordAuthFilter;
import binbean.binbean_BE.auth.filter.JwtVerificationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtVerificationFilter jwtVerificationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests((requests) ->
                requests
                    .requestMatchers(HttpMethod.POST, "/api/auths/registration")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtVerificationFilter, JwtUsernamePasswordAuthFilter.class)
            .httpBasic(HttpBasicConfigurer::disable)
            .formLogin(FormLoginConfigurer::disable);

        return http.build();
    }
}
