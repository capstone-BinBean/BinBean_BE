package binbean.binbean_BE.auth;

import binbean.binbean_BE.constants.Constants;
import binbean.binbean_BE.enums.user.Role;
import binbean.binbean_BE.entity.user.User;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRole().equals(Role.ROLE_ADMIN)) {
            return List.of(new SimpleGrantedAuthority(Constants.User.ROLE_PREFIX + Role.ROLE_ADMIN.name()));
        } else {
            return List.of(new SimpleGrantedAuthority(Constants.User.ROLE_PREFIX + Role.ROLE_USER.name()));
        }
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
