package binbean.binbean_BE.encryption;

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
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final int IV_SIZE = 16; // AES CBC 모드의 IV는 항상 16바이트(128비트)여야만 함

    /**
     * 결과 포맷 : [IV(16바이트) + 암호문]을 Base64로 인코딩
     * 복호화 방식 : 복호화 전에 IV 분리 필요
     *
     * AES/CBC 는 각 블록을 128bit 단위로 암호화
     * IV 값 또한 128bit = 16byte 크기로 정의
     */
    public static String encrypt(String key, String plainText) throws Exception {
        byte[] ivBytes = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(ivBytes);
        // Initialization Vector를 매번 새로 생성하고 암호문에 포함시킴
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        byte[] keyBytes = Base64.getDecoder().decode(key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] encrypted = cipher.doFinal(plainText.getBytes(UTF_8));

        // 결과 = IV + 암호문을 Base64 인코딩
        byte[] result = ByteBuffer.allocate(ivBytes.length + encrypted.length)
            .put(ivBytes)
            .put(encrypted)
            .array();

        return Base64.getEncoder().encodeToString(result);
    }

    public static String decrypt(String key, String cipherText, boolean isKeyBase64Encoded) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        ByteBuffer buffer = ByteBuffer.wrap(decoded);

        byte[] ivBytes = new byte[IV_SIZE];
        buffer.get(ivBytes);
        byte[] cipherBytes = new byte[buffer.remaining()];
        // 실제 암호문 추출
        buffer.get(cipherBytes);

        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        byte[] keyBytes = isKeyBase64Encoded
            ? Base64.getDecoder().decode(key)
            : key.getBytes(UTF_8); // 이게 MASTER_KEY 같은 경우

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        return new String(cipher.doFinal(cipherBytes), UTF_8);
    }
}
