package binbean.binbean_BE.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.auth.filter.JwtVerificationFilter;
import binbean.binbean_BE.constants.Constants.ErrorMsg;
import binbean.binbean_BE.dto.auth.TokenDto;
import binbean.binbean_BE.dto.auth.request.LoginRequest;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.dto.auth.request.SocialLoginRequest;
import binbean.binbean_BE.encryption.AESUtils;
import binbean.binbean_BE.enums.user.Role;
import binbean.binbean_BE.exception.UnauthorizedException;
import binbean.binbean_BE.exception.UserAlreadyExistException;
import binbean.binbean_BE.helper.ObjectMapperUtils;
import binbean.binbean_BE.infra.RedisService;
import binbean.binbean_BE.service.AuthService;
import binbean.binbean_BE.service.UserService;
import binbean.binbean_BE.stub.StubData;
import java.util.Optional;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        String jsonRequest = ObjectMapperUtils.toJsonString(request);

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

        String jsonRequest = ObjectMapperUtils.toJsonString(request);

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

        String jsonRequest = ObjectMapperUtils.toJsonString(request);

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
    @DisplayName("로그인 성공하면 200 상태 코드와 함께 accessToken과 refreshToken을 반환한다")
    void login_Success_Returns_OK() throws Exception {
        //given
        LoginRequest request = StubData.MockAuth.getLoginRequest();
        // 반환될 토큰 정보
        TokenDto tokenDto = StubData.MockAuth.getTokenDto();

        UserDetailsImpl userDetails = new UserDetailsImpl(StubData.MockUser.getUserDetails().getUser());

        // authenticationManager를 mock하여 UserDetailsImpl(실제 로그인한 사용자 정보)을 주체로 인증된 토큰 반환
        // UserDetailsImpl을 사용하여 인증 시도
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        // authenticationManager가 호출될 때, Authentication 객체가 반환하도록 함 (사용자 정상 인증되는 경우)
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willReturn(authentication);

        // JwtTokenProvider mock (반한될 tokenDto 객체 설정)
        given(jwtTokenProvider.generateToken(any(UserDetailsImpl.class))).willReturn(tokenDto);

        String jsonRequest = ObjectMapperUtils.toJsonString(request);

        // when
        ResultActions result = mockMvc.perform(post("/api/auths/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.authType").value("Authorization"))
            .andExpect(jsonPath("$.accessToken").value(tokenDto.getAccessToken()))
            .andExpect(jsonPath("$.refreshToken").value(tokenDto.getRefreshToken()))
            .andExpect(jsonPath("$.grantType").value("Bearer"));

        then(authenticationManager).should(times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        then(jwtTokenProvider).should(times(1)).generateToken(any(UserDetailsImpl.class));
    }

    @Test
    @DisplayName("올바르지 않은 아이디/비밀번호로 로그인 시도할 경우 401 상태 코드를 반환한다")
    void login_Fail_Returns_Unauthorized() throws Exception {
        // given
        LoginRequest request = StubData.MockAuth.getLoginRequestWillFail();

        // 인증 실패 예외가 발생하도록 설정
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willThrow(new BadCredentialsException(ErrorMsg.INVALID_CREDENTIALS));

        String jsonRequest = ObjectMapperUtils.toJsonString(request);

        // when
        ResultActions result = mockMvc.perform(post("/api/auths/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then
        result.andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));

        then(authenticationManager).should(times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("소셜 로그인 성공하면 200 상태 코드와 함께 accessToken과 refreshToken을 반환한다")
    void social_Login_Success_Returns_OK() throws Exception {
        //given
        SocialLoginRequest request = StubData.MockAuth.getSocialLoginRequest();
        // 반환될 토큰 정보
        TokenDto tokenDto = StubData.MockAuth.getTokenDto();

        UserDetailsImpl userDetails = new UserDetailsImpl(StubData.MockUser.getUserDetails().getUser());

        // JwtUsernamePasswordAuthFilter의 authenticateSocialLogin 메서드에 따른 mock 처리
        // authenticationManager 대신 loadUserByUsername 메서드를 통해 사용자 인증 처리
        given(authService.loadUserByUsername(request.email())).willReturn(userDetails);
        given(jwtTokenProvider.generateToken(any(UserDetailsImpl.class))).willReturn(tokenDto);

        String jsonRequest = ObjectMapperUtils.toJsonString(request);

        // when
        ResultActions result = mockMvc.perform(post("/api/auths/kakao/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.authType").value("Authorization"))
            .andExpect(jsonPath("$.accessToken").value(tokenDto.getAccessToken()))
            .andExpect(jsonPath("$.refreshToken").value(tokenDto.getRefreshToken()))
            .andExpect(jsonPath("$.grantType").value("Bearer"));

        then(authService).should(times(1)).loadUserByUsername(request.email());
        then(jwtTokenProvider).should(times(1)).generateToken(any(UserDetailsImpl.class));
    }

    @Test
    @DisplayName("기존 리프레쉬 토큰 만료로 인해 리프레쉬 토큰 재발급 실패하면 401 상태 코드를 반환한다")
    void reissue_Fail_Expired_Returns_Unauthorized() throws Exception {
        // given
        String expiredRefreshToken = "expired-refresh-token";

        // 리프레쉬 토큰 만료 시 UnauthorizedException 던지도록 설정
        doThrow(new UnauthorizedException()).when(authService).reissue(expiredRefreshToken);

        // when
        ResultActions result = mockMvc.perform(post("/api/auths/reissue")
            .header("X-Refresh-Token", expiredRefreshToken));

        // then
        result.andExpect(status().isUnauthorized());

        then(authService).should(times(1)).reissue(expiredRefreshToken);
    }

    @Test
    @DisplayName("리프레쉬 토큰이 Redis에 저장된 값과 일치하지 않음으로 인해 재발급이 실패하면 401 상태코드가 반환된다.")
    void reissue_Fail_Not_Match_with_Redis_Returns_Unauthorized() throws Exception {
        // given
        String refreshToken = "refresh-token";
        String username = "testuser";

        // redis에 저장된 리프레쉬 토큰
        String refreshTokenInRedis = "stored-refresh-tokendsfdfasdfasd";

        // 리프레쉬 토큰이 redis와 일치하지 않으면 UnauthorizedException 발생
        when(jwtTokenProvider.isRefreshTokenMatched(refreshToken, refreshTokenInRedis))
            .thenReturn(false);
        doThrow(new UnauthorizedException()).when(authService).reissue(refreshToken);

        // when
        ResultActions result = mockMvc.perform(post("/api/auths/reissue")
            .header("X-Refresh-Token", refreshToken))
            .andDo(print());

        // then
        result.andExpect(status().isUnauthorized());
        then(authService).should(times(1)).reissue(refreshToken);
    }
}
