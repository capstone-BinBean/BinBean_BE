package binbean.binbean_BE.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.springframework.data.redis.connection.ReactiveStreamCommands.AddStreamRecord.body;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.filter.JwtVerificationFilter;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.encryption.AESUtils;
import binbean.binbean_BE.enums.user.Role;
import binbean.binbean_BE.infra.RedisService;
import binbean.binbean_BE.service.AuthService;
import binbean.binbean_BE.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RedisService redisService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AESUtils aesUtils;

    @TestConfiguration
    static class JwtFilterConfig {
        @Bean
        public JwtVerificationFilter jwtVerificationFilter(JwtTokenProvider jwtTokenProvider,
            RedisService redisService, AuthService authService) {
            // JwtVerificationFilter를 수동으로 빈으로 등록
            return new JwtVerificationFilter(jwtTokenProvider, authService, redisService);
        }
    }

    @BeforeEach
    void setUp() {
        // 필요한 사전 세팅 있으면 여기에 작성
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 초기화 작업이 필요하면 여기에 작성
    }

    @Test
    @DisplayName("회원가입이 완료되면 201 상태값이 반환된다")
    void registerUser_Success_Returns_Created() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest(
            "test@email.com",
            "password123",
            "testNickName",
            "",
            Role.ROLE_USER);
        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        // when & then
        ResultActions result = mockMvc.perform(post("/api/auths/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        result.andExpect(status().isCreated());

        verify(authService).registerUser(any(RegisterRequest.class));
        then(authService).should().registerUser(any(RegisterRequest.class));
    }

}
