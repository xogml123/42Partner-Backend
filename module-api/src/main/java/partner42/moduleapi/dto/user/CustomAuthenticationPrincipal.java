package partner42.moduleapi.dto.user;


import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import partner42.modulecommon.domain.model.user.Role;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.domain.model.user.UserRole;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CustomAuthenticationPrincipal implements UserDetails, OAuth2User, CredentialsContainer,
    Serializable {

    /**
     * 직렬화를 할 때 SUID 선언이 없다면 내부에서 자동으로 유니크한 번호를 생성하여 관리하게 된다. SUID는 직렬화와 역직렬화 과정에서 값이 서로 맞는지 확인한 후에
     * 처리를 하기 때문에 이 값이 맞지 않다면 InvalidClassException 예외가 발생한다.
     * <p>
     * 자바의 직렬화 스펙 정의를 살펴보면 SUID 값은 필수가 아니며 선언되어 있지 않으면 클래스의 기본 해시값을 사용한다.
     */
    private static final long serialVersionUID = 159813599623625L;

    private String apiId;
    private String username;

    private String password;


    @Builder.Default
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    private Boolean enabled = true;

    //transient 해야 serialize되지 않음.
    //Fields in a "Serializable" class should either be transient or serializable
    //java:S1948
    @Builder.Default
    private transient Map<String, Object> attributes = new HashMap<>();

    @Builder.Default
    private Set<SimpleGrantedAuthority> authorities = new HashSet<>();

    @Override
    public String getName() {
        return this.username;
    }

//    @Override
//    public Map<String, Object> getAttributes() {
//        return this.attributes;
//    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public Set<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    public static CustomAuthenticationPrincipal of(User user, Map<String, Object> attributes) {
        return CustomAuthenticationPrincipal.builder()
            .apiId(user.getApiId())
            .username(user.getUsername())
            .password(user.getPassword())
            .attributes(attributes)
            .authorities(user.getUserRoles().stream()
                .map((UserRole::getRole))
                .map(Role::getAuthorities)
                .flatMap(Set::stream)
                .map(authority ->
                    new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toSet())
            )
            .build();
    }

}
