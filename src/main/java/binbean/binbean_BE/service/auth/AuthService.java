package binbean.binbean_BE.service.auth;

import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.dto.auth.request.RegisterRequest;
import binbean.binbean_BE.exception.user.UserAlreadyExistException;
import binbean.binbean_BE.exception.user.UserNotFoundException;
import binbean.binbean_BE.entity.user.User;
import binbean.binbean_BE.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    private User getUserEntity(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
    }
}
