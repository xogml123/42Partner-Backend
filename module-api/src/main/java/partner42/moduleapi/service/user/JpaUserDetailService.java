package partner42.moduleapi.service.user;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.user.CustomAuthenticationPrincipal;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.user.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JpaUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
                return new UsernameNotFoundException("User name: " + username + " not found");
            }
        );
        return CustomAuthenticationPrincipal.of(user, null);

    }

}
