package binbean.binbean_BE.auth.filter;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.auth.dto.TokenDto;
import binbean.binbean_BE.auth.dto.request.LoginRequest;
import binbean.binbean_BE.infra.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
public class JwtUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtUsernamePasswordAuthFilter(AuthenticationManager authenticationManager,
        JwtTokenProvider jwtTokenProvider, RedisService redisService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
    }

    /**
     * 사용자의 인증을 수행하는 메서드
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        // request body GET
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

        // 헤더에 액세스 토큰 추가
        jwtTokenProvider.setHeaderAccessToken(response, accessToken);

        /** 리프레쉬 토큰을 HttpOnly 쿠키에 저장
         * 헤더에 직접 리프레쉬 토큰 저장하지 않고 (보안 낮출 가능성 유)
         * HttpOnly 쿠키를 사용하면 자동으로 요청 시 전달
         */
//        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
//            .httpOnly(true) // 자바스크립트에서 접근 불가능
//            .secure(true) // HTTPS에서만 전송 가능
//            .sameSite("Lax") // CSRF 공격 방지
//            .path("/")
//            .maxAge(jwtTokenProvider.getRefreshExpirationTime())
//            .build();

//        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // FIXME: Redis에 Refresh Token 저장
//        redisService.setStringValue(userDetails.getUsername(), refreshToken,
//            jwtTokenProvider.getRefreshExpirationTime());

        String jsonResponse = objectMapper.writeValueAsString(tokenDto);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
