package partner42.modulecommon.config.auditing;

import partner42.modulecommon.domain.model.user.User;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Auditor  implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null == authentication || !authentication.isAuthenticated()) {
            return Optional.of("system");
        }
        User user = (User) authentication.getPrincipal();
        return Optional.of(user.getUsername());
    }
}
