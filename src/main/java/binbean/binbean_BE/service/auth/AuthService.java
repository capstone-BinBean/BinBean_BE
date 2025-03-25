package binbean.binbean_BE.service.auth;

import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.exception.user.UserAlreadyExistException;
import binbean.binbean_BE.entity.user.User;
import binbean.binbean_BE.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = getUserEntity(username);
        return new UserDetailsImpl(user);
    }

    public void registerUser(RegisterRequest request) {
        userRepository.findByEmail(request.email())
            .ifPresent( user -> {
                throw new UserAlreadyExistException(user.getEmail());
            });

        userRepository.findByNickname(request.nickname())
            .ifPresent(user -> {
                throw new UserAlreadyExistException(user.getNickname());
            });

        var user = request.toEntity();
        user.setPassword(encodePassword(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * 소셜 로그인일 경우, password를 null로 보냄
     */
    private String encodePassword(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        return passwordEncoder.encode(password);
    }

    private User getUserEntity(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
