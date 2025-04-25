package binbean.binbean_BE.encryption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.InvalidKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AESUtilsTest {
    private AESUtils aesUtils;
    private static final String MASTER_KEY = "thisisaverylong32charkeyexample1";
    private static final String ENCRYPTED_AES_KEY = "c+5yzvDWm+zNy3cPmCZvGS4hjSlO5t5icdODHQQrMmjEISBriZGHhiZeSZWr9m8Rcz47ym7F+LTzKvCjT8UIFg==";
    private static final String DECRYPTED_AES_KEY = "eXGcv6UnXsV+sw6F4ekbxLVcH+jPwErFWGNJi8YzeUE=";

    @BeforeEach
    void setUp() {
        // AESUtils의 생성자를 직접 호출하여 인스턴스를 생성합니다.
        aesUtils = new AESUtils();
        aesUtils.setMasterKey(MASTER_KEY);
        aesUtils.setEncryptedAesKey(ENCRYPTED_AES_KEY);
    }

    @Test
    @DisplayName("MASER KEY를 활용한 암호화/복호화 테스트")
    void encryptDecryptTest() throws Exception {
        String plainText = "encryptTest";
        String encrypted = aesUtils.encrypt(MASTER_KEY, plainText);
        String decrypted = aesUtils.decrypt(MASTER_KEY, encrypted, true);

        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEqualTo(plainText);

        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("MASER KEY를 활용한 암호화/복호화 실패 테스트")
    void encryptDecryptTestFailure() throws Exception {
        String invalidMasterKey = "master"; // 마스터 길이가 32바이트 미만
        String plainText = "encryptTest";
        String encrypted = aesUtils.encrypt(MASTER_KEY, plainText);
        assertThatThrownBy(() -> aesUtils.decrypt(invalidMasterKey, encrypted, true))
            .isInstanceOf(InvalidKeyException.class);
    }

    @Test
    @DisplayName("AES키 복호화 테스트")
    void testGetDecryptedAesKey() throws Exception {
        // AES 키 복호화
        String decrypted = aesUtils.decrypt(MASTER_KEY, ENCRYPTED_AES_KEY, true);
        assertThat(decrypted).isEqualTo(DECRYPTED_AES_KEY);
    }

    @Test
    @DisplayName("AES KEY로 암호화/복호화")
    void testEncryptDecryptWithAesKey() throws Exception {
        String plainText = "Sensitive Information";
        String encrypted = aesUtils.encrypt(DECRYPTED_AES_KEY, plainText);

        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEqualTo(plainText);

        String decrypted = aesUtils.decrypt(DECRYPTED_AES_KEY, encrypted, true);
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    @DisplayName("AES 키 복호화 실패 테스트")
    void testDecryptAesKeyFailure() {
        String invalidEncryptedKey = "invalidEncryptedKey";
        assertThatThrownBy(() -> aesUtils.decrypt(MASTER_KEY, invalidEncryptedKey, false))
            .isInstanceOf(RuntimeException.class);
    }
}