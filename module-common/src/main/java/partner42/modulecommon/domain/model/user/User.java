package partner42.modulecommon.domain.model.user;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import partner42.modulecommon.domain.model.BaseTimeVersionEntity;
import partner42.modulecommon.domain.model.member.Member;


@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "USER", uniqueConstraints = {
    @UniqueConstraint(name = "USERNAME_UNIQUE", columnNames = {"username"}),
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"}),
    @UniqueConstraint(name = "EMAIL_UNIQUE", columnNames = {"email"}),
    @UniqueConstraint(name = "SLACK_EMAIL_UNIQUE", columnNames = {"slackEmail"})
})
@Entity
public class User extends BaseTimeVersionEntity implements UserDetails, OAuth2User ,CredentialsContainer, Serializable {
    //********************************* static final 상수 필드 *********************************/
    /**
     * 직렬화를 할 때 SUID 선언이 없다면 내부에서 자동으로 유니크한 번호를 생성하여 관리하게 된다. SUID는 직렬화와 역직렬화 과정에서 값이 서로 맞는지
     * 확인한 후에 처리를 하기 때문에 이 값이 맞지 않다면 InvalidClassException 예외가 발생한다.
     *
     * 자바의 직렬화 스펙 정의를 살펴보면 SUID 값은 필수가 아니며 선언되어 있지 않으면 클래스의 기본 해시값을 사용한다.
     *
     */
    private static final long serialVersionUID = 159813599623625L;
    /**
     * email 뒤에 붙는 문자열
     */
    public static final String GMAIL = "@gmail.com";
    public static final String SEOUL_42 = "@student.42seoul.kr";

    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/

    /**
     * AUTH에 필요한 필드
     */
    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();

    @Column(nullable = false, updatable = false, length = 30)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String oauth2Username;

    @Column(nullable = false,  length = 100)
    private String email;

    @Column(nullable = false)
    private String imageUrl;

    @Column(length = 80)
    private String slackEmail;

    @Builder.Default
    @Column(nullable = false,  length = 100)
    private final OAuth2Type oAuth2Type = OAuth2Type.INTRA_42;

    @Builder.Default
    @Column(nullable = false,  length = 100)
    private Boolean isOAuth2 = true;





    @Builder.Default
    @Column(nullable = false)
    private Boolean accountNonExpired = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean accountNonLocked = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;



    /********************************* 비영속 필드 *********************************/
    @Builder.Default
    @Transient
    private Map<String, Object> attributes = new HashMap<>();

    /********************************* 연관관계 매핑 *********************************/

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

    /**
     * role
     */
    @Singular
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
    private final Set<UserRole>
        userRoles = new HashSet<>();


    /********************************* 연관관계 편의 메서드 *********************************/

    public void setMember(Member member) {
        this.member = member;
    }

    /********************************* 생성 메서드 *********************************/

    public static User of(String username, String encodedPassword, String email,
        String oauth2Username, String imageUrl, Member member) {

        User user = User.builder()
            .username(username)
            .password(encodedPassword)
            .email(email)
            .oauth2Username(oauth2Username)
            .imageUrl(imageUrl)
            .build();
        user.setMember(member);
        return user;
    }



    /********************************* 비니지스 로직 *********************************/


    /**
     *
     * @return
     */
    public Set<SimpleGrantedAuthority> getAuthorities() {
        return userRoles.stream()
            .map((UserRole::getRole))
            .map(Role::getAuthorities)
            .flatMap(Set::stream)
            .map(authority ->
                new SimpleGrantedAuthority(authority.getPermission()))
            .collect(Collectors.toSet());
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public Map<String, Object> getAttributes(){
        return this.attributes;
    }

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


    public void setNewCurrentlyUsedUserRole(UserRole newUserRole) {
        newUserRole.setUserAndAddUserRoleToUser(this);
    }

    public static String getRamdomPassword(int size) {
        char[] charSet = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '!', '@', '#', '$', '%', '^', '&' };

        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());

        int idx = 0;
        int len = charSet.length;
        for (int i=0; i<size; i++) {
            // idx = (int) (len * Math.random());
            idx = sr.nextInt(len);    // 강력한 난수를 발생시키기 위해 SecureRandom을 사용한다.
            sb.append(charSet[idx]);
        }
        int[] start = {0, 10, 36, 62};
        int[] diff = {10, 26, 26, 7};
        for (int i=0; i<4; i++) {
            idx = sr.nextInt(len);
            sb.append(charSet[(idx) % diff[i] + start[i]]);
        }

        return sb.toString();
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

//    public void updateUserByOAuthIfo(String imageUrl) {
//        this.imageUrl = imageUrl;
//
//    }


    /********************************* DTO *********************************/
}

