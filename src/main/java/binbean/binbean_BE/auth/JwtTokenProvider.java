package binbean.binbean_BE.auth;

import binbean.binbean_BE.constants.Constants.ErrorMsg;
import binbean.binbean_BE.constants.Constants.LoggingMsg;
import binbean.binbean_BE.dto.auth.TokenDto;
import binbean.binbean_BE.encryption.AESUtils;
import binbean.binbean_BE.infra.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public static final String BEARER = "Bearer";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";

    private final SecretKey key;
    private final RedisService redisService;

    @Value("${jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey, AESUtils aesUtils,
        RedisService redisService) {
        try {
            String decryptedSecretKey = aesUtils.decryptWithAesKey(secretKey);
            this.key = Keys.hmacShaKeyFor(decryptedSecretKey.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(ErrorMsg.JWT_SECRET_DECRYPT_ERROR, e);
        }
        this.redisService = redisService;
    }

    /**
     * subject를 담아 해당 키로 사인함. jwt 토큰 생성 Access Token 만료시점: 현재로부터 3시간 뒤
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
     * jwt 토큰 복호화하여 subject 추출 (username)
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
     * 토큰의 유효성과 만료 여부 확인 이미 만료된 토큰에서 페이로드를 파싱하는 과정에서 에러가 발생하기 때문에 예외 처리
     */
    public void validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwtToken);
            logger.info("JWT Expiration :", claims.getPayload().getExpiration());
            // exp 날짜가 현재 날짜보다 전에 있지 않으면 토큰 만료
            if (claims.getPayload().getExpiration().before(new Date())) {
                throw new ExpiredJwtException(null, claims.getPayload(), "JWT Token Expired");
            }
        } catch (ExpiredJwtException e) {
            // 만료된 토큰 예외 던지기
            logger.error("Expired JWT token: " + e.getMessage());
            throw e;
        } catch (JwtException e) {
            // 기타 JWT 관련 예외
            logger.error("JWT Exception: ", e);
            throw new JwtException("Invalid JWT token", e); // 일반적인 JWT 오류 예외 던지기
        }
    }

    /**
     * refreshToken 토큰 검증
     * redis에 저장된 토큰을 불러와서 비교
     */
    public boolean validateRefreshToken(String refreshToken, String redisRefreshToken) {
        return StringUtils.hasText(refreshToken) && refreshToken.equals(redisRefreshToken);
    }

    // 액세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(AUTHORIZATION, BEARER_PREFIX + accessToken);
    }

    // 헤더 accessToken 리턴
    public String getHeaderAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader(AUTHORIZATION);
        if (accessToken != null && accessToken.startsWith(BEARER_PREFIX)) {
            return accessToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public long getRefreshExpirationTime() {
        return refreshExpirationTime;
    }

    /**
     * 토큰의 남은 유효시간 반환
     */
    public long getRemainingValidityTime(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        long exp = claims.getExpiration().getTime();
        long now = Instant.now().getEpochSecond();
        return Math.max(exp - now, 0);
    }

    /**
     * 로그아웃을 하였으나 이전에 사용된 액세스 토큰이 아직 유효한 경우,
     * 로그아웃 시에 레디스에 저장한 액세스 토큰값을 불러와 해당 값이 존재하면 (값이 "logout")
     * true를 반환
     * (JWT 토큰의 유효성을 서버에서 강제로 무효화시킬 수 없기에 redis에 따로 저장해두고 요청 시 확인)
     */
    public boolean isAccessTokenLogout(String accessToken) {
        Optional<String> token = redisService.getValues(accessToken);
        return token.isPresent() && token.get().equals(LoggingMsg.LOGOUT_FLAG);
    }
}
