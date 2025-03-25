package binbean.binbean_BE.auth.filter;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.constants.Constants;
import binbean.binbean_BE.constants.Constants.ErrorMsg;
import binbean.binbean_BE.dto.auth.TokenDto;
import binbean.binbean_BE.dto.auth.request.LoginRequest;
import binbean.binbean_BE.exception.ErrorResponse;
import binbean.binbean_BE.infra.RedisService;
import binbean.binbean_BE.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

/**
 * 클라이언트의 로그인 요청을 처리해주는 인증 필터
 * UsernamePasswordAuthenticationFilter를 확장하여 JWT 기반 인증을 수행
 */
//@Component
public class JwtUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtUsernamePasswordAuthFilter(AuthenticationManager authenticationManager,
        AuthService authService, JwtTokenProvider jwtTokenProvider, RedisService redisService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
        this.redisService = redisService;
    }

    /**
     * 사용자의 인증을 수행하는 메서드
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
        try {
            ServletInputStream servletInputStream = request.getInputStream();
            String requestBody = StreamUtils.copyToString(servletInputStream, StandardCharsets.UTF_8);
            // Json data parsing
            LoginRequest loginDto = objectMapper.readValue(requestBody, LoginRequest.class);

            String requestURI = request.getRequestURI();
            // 소셜 로그인
            if (requestURI.contains("/api/auths/kakao/login")) {
                return authenticateSocialLogin(loginDto.email());
            } else {
                // 일반 로그인
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
                return authenticationManager.authenticate(authToken);
            }
        } catch (IOException e) {
            logger.error(e);
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    /**
     * Spring Security가 인증 과정에서 예외를 던지면 자동으로 호출되는 메서드
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        if (failed instanceof UsernameNotFoundException) {
            setErrorResponse(response, HttpStatus.NOT_FOUND, ErrorMsg.USER_NOT_FOUND);
        } else if (failed instanceof BadCredentialsException) {
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, ErrorMsg.INVALID_CREDENTIALS);
        } else {
            setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, failed.getMessage());
        }
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

        setResponseEncoding(response);
        String jsonResponse = objectMapper.writeValueAsString(tokenDto);
        response.getWriter().write(jsonResponse);
    }
    private void setErrorResponse(HttpServletResponse response, HttpStatus status, String message) {
        response.setStatus(status.value());
        setResponseEncoding(response);
        try {
            ErrorResponse errorResponse = new ErrorResponse(status, message);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private void setResponseEncoding(HttpServletResponse response) {
        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
    }

    private Authentication authenticateSocialLogin(String email) {
        UserDetails userDetails = authService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return authToken;
    }
}
