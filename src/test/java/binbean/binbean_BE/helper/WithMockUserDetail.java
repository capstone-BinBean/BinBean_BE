package binbean.binbean_BE.helper;

import binbean.binbean_BE.enums.user.Role;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockSecurityContextFactory.class)
public @interface WithMockUserDetail {
    String email() default "test@email.com";

    String nickname() default "testNickname";

    String password() default "test1234";

    Role role() default Role.ROLE_USER;
}
