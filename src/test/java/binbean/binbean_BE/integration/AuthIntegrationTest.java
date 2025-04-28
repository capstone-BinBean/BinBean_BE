package binbean.binbean_BE.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.data.redis.connection.ReactiveStreamCommands.AddStreamRecord.body;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.auth.filter.JwtVerificationFilter;
import binbean.binbean_BE.dto.auth.TokenDto;
import binbean.binbean_BE.dto.auth.request.LoginRequest;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.encryption.AESUtils;
import binbean.binbean_BE.enums.user.Role;
import binbean.binbean_BE.exception.UserAlreadyExistException;
import binbean.binbean_BE.infra.RedisService;
import binbean.binbean_BE.service.AuthService;
import binbean.binbean_BE.service.UserService;
import binbean.binbean_BE.stub.StubData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

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

    @MockitoBean
    private AuthenticationManager authenticationManager;

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

        // when
        ResultActions result = mockMvc.perform(post("/api/auths/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        result.andExpect(status().isCreated())
            .andExpect(content().string("")); // 응답 body가 빈 문자열인지 확인

        // then
        // mock 객체가 특정 메서드를 호출했는지 검증
        // authService(mock 객체)의 registerUser 메서드가 정확히 1번 호출되어야 한다
        then(authService).should(times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입을 시도하는 경우 409 상태값이 반환된다")
    void registerUser_Fail_Returns_Conflicted() throws Exception {
        // given
        RegisterRequest request = StubData.MockAuth.getRegisterRequest();

        // registerUser 호출 시 강제로 예외 던지게 설정 (mock 객체가 특정 메서드 호출 시 예외를 던지게 미리 세팅)
        doThrow(new UserAlreadyExistException(request.email()))
            .when(authService).registerUser(any(RegisterRequest.class));

        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(post("/api/auths/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then
        result.andExpect(status().isConflict())
            .andExpect(jsonPath("$.message")
                .value("USER : " + request.email() + " already exists"));

        then(authService).should(times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("이미 존재하는 닉네임으로 회원가입을 시도하는 경우 409 상태값이 반환된다")
    void registerUser_Fail_Returns_NickName_Conflicted() throws Exception {
        // given
        RegisterRequest request = StubData.MockAuth.getRegisterRequest();

        doThrow(new UserAlreadyExistException(request.nickname()))
            .when(authService).registerUser(any(RegisterRequest.class));

        String jsonRequest = new ObjectMapper().writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(post("/api/auths/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then
        result.andExpect(status().isConflict())
            .andExpect(jsonPath("$.message")
                .value("USER : " + request.nickname() + " already exists"))
            .andDo(print());

        then(authService).should(times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("로그인 성공하면 200 상태 코드를 반환한다")
    void login_Success_Returns_OK() throws Exception {
//        //given
//        LoginRequest request = StubData.MockAuth.getLoginRequest();
//
//        TokenDto tokenDto = TokenDto.builder()
//            .grantType("Bearer")
//            .accessToken("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJxd2Vyb0BnbWFpbC5jb20iLCJpYXQiOjE3NDU4MjM3MTgsImV4cCI6MTc0NTgzNDUxOH0.3_uU2uoL1p8rTeHa61sTQSZs3j35u3QuwAtTNWJ9KQ7BuMCoOPqYIAZWMY0W3tEu")
//            .refreshToken("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJxd2Vyb0BnbWFpbC5jb20iLCJpYXQiOjE3NDU4MjM3MTgsImV4cCI6MTc0NjQyODUxOH0.fVUGVRW_KkkqYGRUN0sDnw2UjPaT4whERvIdmn3aepVshADqwoDhmlSAisfEgnnx")
//            .authType("Authorization")
//            .build();
//
//        // 인증 성공 모킹
//        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//            .willReturn(new UsernamePasswordAuthenticationToken("user", null, new ArrayList<>()));
////        given(jwtTokenProvider.generateToken(any(UserDetailsImpl.class))).willReturn(tokenDto);
//
//        String jsonRequest = new ObjectMapper().writeValueAsString(request);
//
//        // when
//        ResultActions result = mockMvc.perform(post("/api/auths/login")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(jsonRequest));
//
//        // then
//        result.andExpect(status().isOk())
//            .andExpect(header().exists("Authorization")) // 액세스 토큰 헤더 있는지 확인
//            .andExpect(jsonPath("$.accessToken").value(tokenDto.getAccessToken()))
//            .andExpect(jsonPath("$.refreshToken").value(tokenDto.getRefreshToken()))
//            .andExpect(jsonPath("$.grantType").value("Bearer"));

//        then(authenticationManager).should().authenticate(any(UsernamePasswordAuthenticationToken.class));
//        then(jwtTokenProvider).should().generateToken(any(UserDetailsImpl.class));

//        then(authenticationManager).should(times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        then(jwtTokenProvider).should(times(1)).generateToken(any(UserDetailsImpl.class));

    }

}
