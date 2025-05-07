package binbean.binbean_BE.integration;

import binbean.binbean_BE.auth.JwtTokenProvider;
import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.dto.FloorList;
import binbean.binbean_BE.dto.request.ChangePasswordRequest;
import binbean.binbean_BE.dto.request.FloorPlanRegisterRequest;
import binbean.binbean_BE.encryption.AESUtils;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.Favorites;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.helper.ObjectMapperUtils;
import binbean.binbean_BE.repository.CafeRepository;
import binbean.binbean_BE.repository.FavoritesRepository;
import binbean.binbean_BE.repository.UserRepository;
import binbean.binbean_BE.service.FloorPlanService;
import binbean.binbean_BE.service.ImageStorageService;
import binbean.binbean_BE.service.UserService;
import binbean.binbean_BE.stub.StubData;
import binbean.binbean_BE.stub.StubData.MockFavorites;
import binbean.binbean_BE.stub.StubData.MockUser;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AESUtils aesUtils;

    @MockitoBean
    private ImageStorageService imageStorageService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private FloorPlanService floorPlanService;
    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private FavoritesRepository favoritesRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // mock user 세팅
        testUser = StubData.MockUser.getNewUser();
        UserDetails userDetails = new UserDetailsImpl(testUser);
        String encodedPassword = passwordEncoder.encode(testUser.getPassword());
        testUser.setPassword(encodedPassword);
        userRepository.save(testUser);

        // UserDetailsImpl을 사용하여 인증 시도 (수동 인증, 추후 @WithUserDetails로 리팩토링 예정)
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

        User updatedUser = userRepository.findByEmail("newUser@email.com").orElseThrow();
        assertEquals("https://s3.example.com/profile.jpg", updatedUser.getProfile());
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

    @Test
    @DisplayName("유저의 비밀번호 변경이 성공적으로 완료되면 200 상태값이 반환된다")
    void change_Password_Success_Returns_OK() throws Exception {
        // given
        String currentPassword = "password123";
        String newPassword = "newPassword123";

        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

        // when
        String jsonRequest = ObjectMapperUtils.toJsonString(request);

        ResultActions result = mockMvc.perform(put("/api/users/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then
        result.andExpect(status().isOk());

        // DB에서 비밀번호가 실제로 변경되었는지 검증
        User updatedUser = userRepository.findByEmail("newUser@email.com").orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }

    @Test
    @DisplayName("현재 비밀번호를 틀리게 입력해서 비밀번호 변경이 실패하면 400 상태값이 반환된다")
    void change_Password_Fails_Returns_Bad_Request() throws Exception {
        // given
        String incorrectCurrentPassword = "incorrectPassword123";
        String newPassword = "newPassword123";
        ChangePasswordRequest request = new ChangePasswordRequest(incorrectCurrentPassword, newPassword);

        // when
        String jsonRequest = ObjectMapperUtils.toJsonString(request);
        ResultActions result = mockMvc.perform(put("/api/users/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.message").value("현재 비밀번호가 일치하지 않습니다."));

        // 실제로 비밀번호가 변경되지 않았는지 DB에서 확인
        User user = userRepository.findByEmail("newUser@email.com").orElseThrow();
        // 원래 비밀번호와 인코딩된 유저 비밀번호 비교
        assertTrue(passwordEncoder.matches("password123", user.getPassword()));
    }

    @Test
    @DisplayName("소셜 로그인 계정일 경우 비밀번호 변경 시도 시 403 상태값이 반환된다")
    void change_Password_Fails_Returns_Forbidden() throws Exception {
        // given
        User user = userRepository.findByEmail("newUser@email.com").orElseThrow();
        // 테스트 유저 비밀번호 null로 설정 (소셜 로그인 계정)
        user.setPassword("");
        userRepository.save(user);
        // 새 비밀번호
        String newPassword = "newPassword123";

        ChangePasswordRequest request = new ChangePasswordRequest(testUser.getPassword(), newPassword);

        // when
        String jsonRequest = ObjectMapperUtils.toJsonString(request);
        ResultActions result = mockMvc.perform(put("/api/users/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then
        result.andExpect(status().isForbidden());
        result.andExpect(jsonPath("$.message").value("소셜 로그인 계정은 비밀번호를 변경할 수 없습니다."));
    }

    @Test
    @DisplayName("즐겨찾기한 카페 좌석 위치 리스트를 성공적으로 불러오면 200 상태값이 반환된다")
    void get_Favorite_Seats_Success_Returns_OK() throws Exception {
        // given
        // 좌석 위치 즐겨찾기 등록
        User ownerUser = userRepository.save(MockUser.getRandomUser());
        Cafe cafe = cafeRepository.save(MockFavorites.getMockCafe(ownerUser));
        FloorPlan fp = MockFavorites.getMockFloorPlan(cafe);
        FloorList floorList = MockFavorites.getMockFloorList(fp);

        FloorPlanRegisterRequest request = new FloorPlanRegisterRequest(floorList, 1, fp.getMaxSeats());
        floorPlanService.saveFloorPlan(List.of(request), cafe);

        Favorites favorite = Favorites.create(testUser, cafe, 3L);
        favoritesRepository.save(favorite);

        // when
        ResultActions result = mockMvc.perform(get("/api/users/favorites")
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.length()").value(1));
        result.andExpect(jsonPath("$[0].cafeName").value("스타벅스 신촌점"));
    }
}

