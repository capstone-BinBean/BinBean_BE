package binbean.binbean_BE.controller;

import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.dto.FloorList;
import binbean.binbean_BE.dto.request.CafeRegisterRequest;
import binbean.binbean_BE.dto.request.CafeUpdateRequest;
import binbean.binbean_BE.dto.request.FloorPlanRegisterRequest;
import binbean.binbean_BE.dto.request.FloorPlanUpdateRequest;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.enums.user.Role;
import binbean.binbean_BE.service.CafeService;
import binbean.binbean_BE.service.FloorPlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CafeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CafeService cafeService;

    @Mock
    private FloorPlanService floorPlanService;

    @InjectMocks
    private CafeController cafeController;

    private final HandlerMethodArgumentResolver authPrincipalResolver =
        new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
                return parameter.hasParameterAnnotation(
                    org.springframework.security.core.annotation.AuthenticationPrincipal.class)
                    && parameter.getParameterType().equals(UserDetailsImpl.class);
            }

            @Override
            public Object resolveArgument(org.springframework.core.MethodParameter parameter,
                ModelAndViewContainer mavContainer,
                NativeWebRequest webRequest,
                WebDataBinderFactory binderFactory) {
                User admin = User.builder()
                    .email("test@test.com")
                    .password("password")
                    .nickname("nickname")
                    .role(Role.ROLE_ADMIN)
                    .build();
                return new UserDetailsImpl(admin);
            }
        };

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
            .standaloneSetup(cafeController)
            .setCustomArgumentResolvers(authPrincipalResolver)
            .build();
    }

    @Test
    @DisplayName("POST /api/cafes/registration - 성공 시 201 반환")
    void registerCafe_Success() throws Exception {
        CafeRegisterRequest cafeRegisterRequest = new CafeRegisterRequest(
            "TestCafe", "TestAddress", 11.11, 12.12,
            "010-1234-5678", 1, 1, 1, 1, "description",
            "09:00", "18:00", "09:00", "18:00", "09:00", "18:00",
            "09:00", "18:00", "09:00", "18:00", "09:00", "18:00", "09:00", "18:00"
        );
        FloorPlanRegisterRequest floorPlanRegisterRequest = new FloorPlanRegisterRequest(
            FloorList.create(
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList()
            ),
            1, 50
        );

        doNothing().when(cafeService)
            .registerCafe(eq(cafeRegisterRequest), eq(List.of(floorPlanRegisterRequest)), isNull(), any(User.class));

        MockMultipartFile cafePart = new MockMultipartFile(
            "cafe", "cafe", "application/json",
            objectMapper.writeValueAsBytes(cafeRegisterRequest)
        );
        MockMultipartFile floorPart = new MockMultipartFile(
            "floorPlan", "floorPlan", "application/json",
            objectMapper.writeValueAsBytes(List.of(floorPlanRegisterRequest))
        );

        mockMvc.perform(multipart("/api/cafes/registration")
                .file(cafePart)
                .file(floorPart)
            )
            .andExpect(status().isCreated());

        then(cafeService).should().registerCafe(any(), any(), any(), any());
    }

    @Test
    @DisplayName("PUT /api/cafes - 성공 시 200 반환")
    void updateCafeInfo_Success() throws Exception {
        CafeUpdateRequest cafeUpdateRequest = new CafeUpdateRequest(
            "UpdateCafe", "UpdateAddress", "010-9999-8888",
            1, 1, 0, 0, "NewDesc",
            "10:00", "19:00", "10:00", "19:00", "10:00", "19:00",
            "10:00", "19:00", "10:00", "19:00", "10:00", "19:00", "10:00", "19:00"
        );

        doNothing().when(cafeService)
            .updateCafeInfo(eq(cafeUpdateRequest), isNull(), any(User.class));

        MockMultipartFile updPart = new MockMultipartFile(
            "cafe", "cafe", "application/json",
            objectMapper.writeValueAsBytes(cafeUpdateRequest)
        );

        mockMvc.perform(multipart("/api/cafes")
                .file(updPart)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
            )
            .andExpect(status().isOk());

        then(cafeService).should().updateCafeInfo(any(), any(), any());
    }

    @Test
    @DisplayName("PUT /api/cafes/floor-plan - 성공 시 200 반환")
    void updateFloorPlan_Success() throws Exception {
        FloorPlanUpdateRequest floorPlanUpdateRequest = new FloorPlanUpdateRequest(
            FloorList.create(
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList()
            ),
            1, 60
        );

        doNothing().when(floorPlanService)
            .updateFloorPlan(eq(List.of(floorPlanUpdateRequest)), any(User.class));

        String json = objectMapper.writeValueAsString(List.of(floorPlanUpdateRequest));

        mockMvc.perform(put("/api/cafes/floor-plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk());

        then(floorPlanService).should().updateFloorPlan(any(), any());
    }
}
