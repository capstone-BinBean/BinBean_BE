package binbean.binbean_BE.encryption;

import binbean.binbean_BE.constants.Constants.ErrorMsg;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AESUtils {
    // AES 대칭키, CBC 모드, PKCS5 패딩
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    // AES CBC 모드의 IV는 항상 16바이트(128비트)여야만 함
    private static final int IV_SIZE = 16;

    private String MASTER_KEY;

    @Value("${binbean.master-key}")
    public void setMasterKey(String key) {
        MASTER_KEY = key;
    }

    @Value("${aes.key}")
    private String encryptedAesKey;

    /**
     * 암호화 메서드 (MASTER KEY로)
     * 평문 데이터를 AES 방식으로 암호화
     *
     * 결과 포맷 : [IV(16바이트) + 암호문]을 Base64로 인코딩
     * 복호화 방식 : 복호화 전에 IV 분리 필요
     *
     * AES/CBC 는 각 블록을 128bit 단위로 암호화
     * IV 값 또한 128bit = 16byte 크기로 정의
     */
    public String encrypt(String key, String plainText) throws Exception {
        byte[] ivBytes = new byte[IV_SIZE];
        // 보안용 난수 생성기를 통해 무작위 16바이트 IV를 생성
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);
        /* Initialization Vector를 SecureRandom으로 생성하여 암호문을 매번 새로 생성하고 암호문에 포함시킴
            (같은 평문, 키로 암호화해도 매번 다른 암호문 생성됨) => 공격자 분석 어려움
         */
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        byte[] keyBytes = Base64.getDecoder().decode(key);
        // AES 비밀키 객체 생성
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        // 암호화 인스턴스 생성하고 암호화 모드로 초기화
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] encrypted = cipher.doFinal(plainText.getBytes(UTF_8));

        // 결과 = IV + 암호문을 하나의 배열로 결합
        byte[] result = ByteBuffer.allocate(ivBytes.length + encrypted.length)
            .put(ivBytes)
            .put(encrypted)
            .array();

        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * 복호화 메서드 (MASTER_KEY로)
     * [IV + 암호문] 구조에서 IV를 분리하여 AES 복호화 수행
     */
    public String decrypt(String key, String cipherText, boolean isKeyBase64Encoded) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        ByteBuffer buffer = ByteBuffer.wrap(decoded);

        byte[] ivBytes = new byte[IV_SIZE];
        // 앞 16 바이트 IV 추출
        buffer.get(ivBytes);
        byte[] cipherBytes = new byte[buffer.remaining()];
        // 실제 암호문 추출
        buffer.get(cipherBytes);

        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        // BASE64로 인코딩되어있는 키인 경우 디코딩 OR 평문 키인 경우 그대로 UTF-8 바이트 변환
        byte[] keyBytes = isKeyBase64Encoded
            ? Base64.getDecoder().decode(key)
            : MASTER_KEY.getBytes(UTF_8);

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        return new String(cipher.doFinal(cipherBytes), UTF_8);
    }

    /**
     * 복호화된 AES 키를 반환하는 메서드 (MASTER_KEY 사용하여 AES 키 복호화)
     */
    public String getDecryptedAesKey() {
        try {
            // (MASTER_KEY는 Base64 아님)
            String decrypted = decrypt(MASTER_KEY, encryptedAesKey, false);
            // 유효성 검사 (AES 키는 복호화 시 BASE64 형식이어야 함)
            Base64.getDecoder().decode(decrypted);
            return decrypted;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(ErrorMsg.AES_KEY_NOT_BASE64, e);
        } catch (Exception e) {
            throw new RuntimeException(ErrorMsg.MASTER_KEY_DECRYPT_ERROR, e);
        }
    }

    /**
     * AES 키로 민감 정보(JWT, Refresh Token 등)를 암호화
     */
    public String encryptWithAesKey(String plainText) {
        try {
            String aesKey = getDecryptedAesKey();
            return encrypt(aesKey, plainText);
        } catch (Exception e) {
            throw new RuntimeException(ErrorMsg.AES_KEY_ENCRYPT_ERROR, e);
        }
    }

    /**
     * AES 키로 민감 정보(JWT, Refresh Token 등)를 복호화
     */
    public String decryptWithAesKey(String encryptedText) {
        try {
            // AES key는 base64로 인코딩된 상태
            String aesKey = getDecryptedAesKey();
            return decrypt(aesKey, encryptedText, true);
        } catch (Exception e) {
            throw new RuntimeException(ErrorMsg.AES_KEY_DECRYPT_ERROR, e);
        }
    }
}
