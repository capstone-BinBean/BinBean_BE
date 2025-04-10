package binbean.binbean_BE.encryption;

import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

// 개발용
@Slf4j
public class AESKeyGenerator {
    @Value("${binbean.master-key}")
    private static String MASTER_KEY;
    @Value("${aes.key}")
    private static String encryptedAesKey;
    @Value("${jwt.secret-key})")
    private static String jwtSecretKey;
    public static void main(String[] args) throws Exception {
        AESUtils aesUtils = new AESUtils();

//        try {
//            // AES-256bits 키 생성
//            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//            keyGen.init(256);
//
//            SecretKey generateKey = keyGen.generateKey();
//            byte[] keyBytes = generateKey.getEncoded();
//            String base64Key = Base64.getEncoder().encodeToString(keyBytes);
//            log.info("Base64 Encoded AES Key: {}", base64Key);
//
//            // AES로 암호화하고 Base64 인코딩
//            var encryptedKey = AESUtils.encrypt(MASTER_KEY, base64Key, true);
//            log.info("암호화된 Encoded AES Key: {}", encryptedKey);
//
//            var decryptedKey = AESUtils.decrypt(MASTER_KEY, encryptedKey, false);
//
//            log.info("복호화된 Encoded AES Key: {}", decryptedKey);
//
//        } catch (Exception e) {
//            e.printStackTrace(); // ← 실제 예외 출력
//            log.error("에러 발생: {}", e.getMessage());
//        }

        try {
            // 1. AES 키 복호화 (MASTER_KEY는 Base64 아님)
            String aesKey = aesUtils.decrypt(MASTER_KEY, encryptedAesKey, false);
            log.info("복호화된 aesKey:{}", aesKey);
            log.info("BASE64 디코딩된 aesKey:{}", Base64.getDecoder().decode(aesKey.getBytes()));

            // 3. JWT Secret Key 암호화 (AES 키 사용)
            String encryptedJwtSecret = aesUtils.encrypt(aesKey, jwtSecretKey);
            log.info("암호화된 JWT Secret Key: {}", encryptedJwtSecret);

            // aesKey로 JWT secret 복호화 (aesKey는 Base64 인코딩된 AES key)
            String decryptedJwtSecret = aesUtils.decrypt(aesKey, encryptedJwtSecret, true);
            log.info("복호화된 JWT Secret Key: {}", decryptedJwtSecret);
        } catch (Exception e) {
            throw new RuntimeException("JWT 시크릿 키 복호화 실패", e);
        }
    }
}
