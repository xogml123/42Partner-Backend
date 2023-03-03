package partner42.modulecommon.config.jpa;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@EnableJpaAuditing
@Component
public class Auditor  implements AuditorAware<String> {
    private static final String NOT_USER = "system";

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication || !authentication.isAuthenticated()) {
            return Optional.of(NOT_USER);
        }
        String username = ((UserDetails)authentication.getPrincipal()).getUsername();
        return Optional.of(username);
    }
}
