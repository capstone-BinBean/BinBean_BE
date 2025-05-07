package binbean.binbean_BE.stub;

import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.dto.FloorList;
import binbean.binbean_BE.dto.Position;
import binbean.binbean_BE.dto.auth.TokenDto;
import binbean.binbean_BE.dto.auth.request.LoginRequest;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.dto.auth.request.SocialLoginRequest;
import binbean.binbean_BE.dto.response.FavoritesResponse;
import binbean.binbean_BE.entity.Cafe;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.entity.floor_plan.FloorPlan;
import binbean.binbean_BE.enums.user.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class StubData {
    public static class MockAuth {

        public static RegisterRequest getRegisterRequest() {
            return new RegisterRequest(
                "test@email.com",
                "password123",
                "testNickName",
                "",
                Role.ROLE_USER
            );
        }

        public static LoginRequest getLoginRequest() {
            return new LoginRequest(
                "test@email.com",
                "password123"
            );
        }

        public static LoginRequest getLoginRequestWillFail() {
            return new LoginRequest(
                "test@email.com",
                "dldl"
            );
        }

        public static SocialLoginRequest getSocialLoginRequest() {
            return new SocialLoginRequest(
                "test@email.com"
            );
        }

        public static TokenDto getTokenDto() {
            return TokenDto.builder()
                .grantType("Bearer")
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .authType("Authorization")
                .build();
        }
    }

    public static class MockUser {
        public static UserDetailsImpl getUserDetails() {
            /**
             * 영속 상태의 객체 (이 객체의 User를 repsitory.save() 하면 다시 병합되기 때문에 충돌 일어날 수 있음)
             * @Transactional 테스트 환경에서는 영속성 컨텍스트가 자동 유지되기 때문에,
             * 같은 엔티티를 두 번 저장하거나 조작하면 충돌 발생
             */
            return new UserDetailsImpl(new User(
                Long.valueOf("1"),
                "test@email.com",
                    "password123",
                "testNickName",
                "",
                Role.ROLE_USER
            ));
        }

        /**
         * 테스트 시 userRepository에 save()해야할 경우 사용
         * ID 없는 새 객체 반환
         * @Transactional 환경에서 동일한 엔티티를 여러 번 save 하지 않도록 함
         */
        public static User getNewUser() {
            // ID는 null이어야 함
            return new User(null,
                "newUser@email.com",
                "password123",
                "newNickName",
                "",
                Role.ROLE_USER);
        }

        /**
         * 통합 테스트에서 동일한 계정(중복된 유저 정보)의 케이스가 병렬 또는 순차적으로 실행되면
         * 충돌할 수 있기에 이를 방지하기 위해 사용
         * ex) 즐겨찾기 기능, 카페 소유자 설정 등 유저 여러 명이 필요할 때 사용
         */
        public static User getRandomUser() {
            // ID는 null이어야 함
            return new User(null,
                UUID.randomUUID() + "@email.com",
                "password123",
                UUID.randomUUID() + "nickname",
                "",
                Role.ROLE_USER);
        }
    }

    public static class MockFavorites {

        public static Cafe getMockCafe(User user) {
            return Cafe.create(user, "스타벅스 신촌점", "서울시 마포구 ...",
                37.882, 127.730, "010-0000-0000", 1, 0, 1,
                0, "신촌점에 위치해있습니다.");
        }

        public static FloorPlan getMockFloorPlan(Cafe cafe) {
            return  FloorPlan.create(cafe, 1, 15);
        }

        public static FloorList getMockFloorList(FloorPlan floorPlan) {
            List<Position> seatPositions = new ArrayList<>();
            for (int i = 0; i < floorPlan.getMaxSeats(); i++) {
                double randomX = ThreadLocalRandom.current().nextDouble(1, 101);
                double randomY = ThreadLocalRandom.current().nextDouble(1, 101);
                Position pos = Position.create(randomX, randomY);
                seatPositions.add(pos);
            }

           return FloorList.create(List.of(), seatPositions, List.of(),
                List.of(), List.of(), List.of());
        }
    }
}
