package binbean.binbean_BE.entity.user;

import binbean.binbean_BE.enums.user.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column
    private String profile; // profile 사진 url

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.ROLE_USER; // 기본값

    public void setPassword(String encoded) {
        this.password = encoded;
    }
}
