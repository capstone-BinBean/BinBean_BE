package binbean.binbean_BE.user.repository;

import binbean.binbean_BE.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
}
