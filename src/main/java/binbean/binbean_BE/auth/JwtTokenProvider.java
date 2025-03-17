package binbean.binbean_BE.auth;

import binbean.binbean_BE.auth.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public static final String BEARER = "Bearer";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";

    private final SecretKey key;

    @Value("${jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     subject를 담아 해당 키로 사인함. jwt 토큰 생성
     Access Token 만료시점: 현재로부터 3시간 뒤
     */
    private TokenDto generateAccessRefreshToken(String subject) {
        Instant now = Instant.now();
        Instant accessExpiresAt = now.plusMillis(accessExpirationTime);
        Instant refreshExpiresAt = now.plusMillis(refreshExpirationTime);

        String accessToken = Jwts.builder()
            .subject(subject)
            .signWith(key)
            .issuedAt(Date.from(now))
            .expiration(Date.from(accessExpiresAt))
            .compact();

        String refreshToken = Jwts.builder().subject(subject)
            .signWith(key)
            .issuedAt(Date.from(now))
            .expiration(Date.from(refreshExpiresAt))
            .compact();

        return TokenDto.builder()
            .grantType(BEARER)
            .authType(AUTHORIZATION)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    /**
     jwt 토큰 복호화하여 subject 추출 (username)
     */
    private String getSubject(String token) {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        } catch (JwtException e) {
            logger.error("JWT Exception :", e);
            throw e;
        }
    }

    /**
     * 실제 jwt 인증에서 사용될 함수
     */
    public TokenDto generateToken(UserDetails userDetails) {
        return generateAccessRefreshToken(userDetails.getUsername());
    }

    /**
     * accessToken으로부터 subject(=username)을 추출하는 함수
     */
    public String getUsername(String token) {
        return getSubject(token);
    }

    /**
     * 토큰의 유효성과 만료 여부 확인
     * 이미 만료된 토큰에서 페이로드를 파싱하는 과정에서 에러가 발생하기 때문에 예외 처리
     */
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwtToken);
            logger.info("JWT Expiration :", claims.getPayload().getExpiration());
            // exp 날짜가 현재 날짜보다 전에 있지 않으면 토큰 만료
            return !claims.getPayload().getExpiration().before(new Date());
        } catch (Exception e) {
            logger.error("JWT Exception :", e);
            return false;
        }
    }

    // 액세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(AUTHORIZATION, BEARER_PREFIX + accessToken);
    }
}
