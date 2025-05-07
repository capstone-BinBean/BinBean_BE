package binbean.binbean_BE.integration;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.encryption.AESUtils;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.infra.RedisService;
import binbean.binbean_BE.repository.UserRepository;
import binbean.binbean_BE.service.AuthService;
import binbean.binbean_BE.service.ImageStorageService;
import binbean.binbean_BE.service.UserService;
import binbean.binbean_BE.stub.StubData;
import jakarta.persistence.EntityManager;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AESUtils aesUtils;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private ImageStorageService imageStorageService;

    @Autowired
    private EntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        // mock user 세팅
        testUser = StubData.MockUser.getNewUser();
        UserDetails userDetails = new UserDetailsImpl(testUser);
        userRepository.save(testUser);

        // UserDetailsImpl을 사용하여 인증 시도
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("사용자 프로필 이미지 업로드가 완료되면 200 상태값이 반환된다")
    void upload_Profile_Image_Success_Returns_OK() throws Exception {
        String imageUrl = "https://s3.example.com/profile.jpg";

        // given
        MockMultipartFile multipartFile = new MockMultipartFile(
            "profileImgUrl",
            "profile.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "profile.jpg".getBytes()
        );

        given(imageStorageService.uploadImage(any(MultipartFile.class))).willReturn(imageUrl);

        // when
        ResultActions result = mockMvc.perform(multipart(HttpMethod.PUT, "/api/users/profile-img")
            .file(multipartFile)
            .with(csrf()));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자 프로필 이미지를 누락하고 업로드하면 400 상태값이 반환된다")
    void upload_Profile_Image_Success_Returns_Bad_Request() throws Exception {
        // given
        // 이미지 없이 요청

        // when
        ResultActions result = mockMvc.perform(multipart(HttpMethod.PUT, "/api/users/profile-img")
            .file("profileImgUrl", new byte[0])
            .with(csrf()));

        // then
        result.andExpect(status().isBadRequest());
    }
}

