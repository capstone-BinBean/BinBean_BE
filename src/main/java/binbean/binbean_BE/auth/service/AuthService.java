package binbean.binbean_BE.auth.service;

import binbean.binbean_BE.auth.dto.request.RegisterRequest;
import binbean.binbean_BE.exception.user.UserAlreadyExistException;
import binbean.binbean_BE.user.entity.User;
import binbean.binbean_BE.user.repository.UserRepository;
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
        return null;
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
}
