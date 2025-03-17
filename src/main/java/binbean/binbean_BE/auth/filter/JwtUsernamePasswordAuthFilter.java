package binbean.binbean_BE.auth.filter;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.auth.dto.TokenDto;
import binbean.binbean_BE.auth.dto.request.LoginRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

/**
 * 클라이언트의 로그인 요청을 처리해주는 인증 필터
 * UsernamePasswordAuthenticationFilter를 확장하여 JWT 기반 인증을 수행
 */
//@RequiredArgsConstructor
public class JwtUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtUsernamePasswordAuthFilter(AuthenticationManager authenticationManager,
        JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 사용자의 인증을 수행하는 메서드
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        // request body GET
        ObjectMapper objectMapper = new ObjectMapper();
        ServletInputStream servletInputStream;
        String requestBody;

        try {
            servletInputStream = request.getInputStream();
            requestBody = StreamUtils.copyToString(servletInputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

        // Json data parsing
        LoginRequest loginDto;
        try {
            loginDto = objectMapper.readValue(requestBody, LoginRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
        return authenticationManager.authenticate(authToken);
    }

    /**
     * 인증이 성공적으로 완료되면 실행되는 메소드
     */
    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 토큰 생성
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        TokenDto tokenDto = jwtTokenProvider.generateToken(userDetails);
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        // 인증 정보를 SecurityContext에 저장하여 이후 요청에서도 유지
        SecurityContextHolder.getContext().setAuthentication(authResult);

        // 헤더에 액세스 토큰 추가
        jwtTokenProvider.setHeaderAccessToken(response, accessToken);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(tokenDto);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
