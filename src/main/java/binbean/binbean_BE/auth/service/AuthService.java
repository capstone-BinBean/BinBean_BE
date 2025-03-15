package binbean.binbean_BE.auth.service;

import binbean.binbean_BE.auth.dto.request.RegisterRequest;
import binbean.binbean_BE.user.entity.User;
import binbean.binbean_BE.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public void registerUser(RegisterRequest request) {
        // FIXME : 이메일 중복체크
        userRepository.findByEmail(request.email())
            .ifPresent( user -> {
                throw new RuntimeException();
            });

        // FIXME : 닉네임 중복체크
        userRepository.findByNickname(request.nickname())
            .ifPresent(user -> {
                throw new RuntimeException();
            });

        userRepository.save(request.toEntity());
    }
}
