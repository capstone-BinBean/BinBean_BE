package binbean.binbean_BE.auth.filter;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.constants.Constants;
import binbean.binbean_BE.constants.Constants.LoggingMsg;
import binbean.binbean_BE.constants.Constants.URL;
import binbean.binbean_BE.service.auth.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    public JwtVerificationFilter(JwtTokenProvider jwtTokenProvider, AuthService authService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI().trim().toLowerCase();

        var accessToken = jwtTokenProvider.getHeaderAccessToken(request);

        // Access Token이 없는 경우 바로 요청 통과
        if (accessToken == null) {
            logger.warn(LoggingMsg.ACCESS_TOKEN_MISSING + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var securityContext = SecurityContextHolder.getContext();

            // 유효한 토큰인지 검사 (유효하지 않으면 예외 발생)
            jwtTokenProvider.validateToken(accessToken);
            var username = jwtTokenProvider.getUsername(accessToken);
            var userDetails = authService.loadUserByUsername(username);
            // 액세스토큰 값이 유효하면 setAuthentication 통해 securityContext에 인증 정보 저장
            setAuthentication((UserDetailsImpl) userDetails, request, securityContext);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰일 경우
            SecurityContextHolder.clearContext();
            // JwtExceptionFilter에서 예외 처리하도록 던짐
            throw e;
        } catch (JwtException e) {
            // 기타 JWT 예외일 경우
            throw e;
        }

        filterChain.doFilter(request, response);
    }

    public void setAuthentication(UserDetailsImpl userDetails, HttpServletRequest request, SecurityContext securityContext) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // SecurityContext에 인증 정보 저장
        securityContext.setAuthentication(authenticationToken);
    }
}
