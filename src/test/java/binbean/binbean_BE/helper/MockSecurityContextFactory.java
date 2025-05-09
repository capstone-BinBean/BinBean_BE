package binbean.binbean_BE.helper;

import binbean.binbean_BE.auth.UserDetailsImpl;
import binbean.binbean_BE.entity.User;
import binbean.binbean_BE.stub.StubData;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class MockSecurityContextFactory implements WithSecurityContextFactory<WithMockUserDetail> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserDetail annotation) {
        UserDetailsImpl mockUser = StubData.MockUser.getUserDetails();

//        UserDetailsImpl principalDetails = new UserDetailsImpl(mockUser);

        // UserDetailsImpl을 사용하여 인증 시도
        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
        //        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
