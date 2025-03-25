package binbean.binbean_BE.auth.filter;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.infra.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

//@Component
public class UrlBasedAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUsernamePasswordAuthFilter loginFilter;
    private final JwtUsernamePasswordAuthFilter socialLoginFilter;

    public UrlBasedAuthenticationFilter(JwtUsernamePasswordAuthFilter loginFilter, JwtUsernamePasswordAuthFilter socialLoginFilter) {
        this.loginFilter = loginFilter;
        this.socialLoginFilter = socialLoginFilter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI().trim().toLowerCase();

        // 일반 로그인 필터 처리
        if (requestURI.contains("/api/auths/login")) {
            loginFilter.doFilter(request, response, filterChain);
        } else if (requestURI.contains("/api/auths/kakao/login")) {
            // 소셜 로그인 필터 처리
            socialLoginFilter.doFilter(request, response, filterChain);
        } else {
            // 그 외 요청은 기본 필터를 그대로 실행
            filterChain.doFilter(request, response);
        }
    }
}
