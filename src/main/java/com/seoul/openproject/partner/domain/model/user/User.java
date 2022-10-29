package com.seoul.openproject.partner.domain.model.user;

import com.seoul.openproject.partner.domain.model.BaseTimeVersionEntity;
import com.seoul.openproject.partner.domain.model.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
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
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;


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
     * email 뒤에 붙는 문자열
     */


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

    public static User createDefaultUser(String username, String encodedPassword, String email,
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

//    public void updateUserByOAuthIfo(String imageUrl) {
//        this.imageUrl = imageUrl;
//
//    }


    /********************************* DTO *********************************/

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    static public class UserDto {
        @Schema(name = "userId" , example = "db688a4a-2f70-4265-a1ea-d15fd6c5c914", description = "사용자 id, 로그인시에 보내지는 값")
        @NotBlank
        private String userId;

        @Schema(name = "oauth2Username" , example = "takim", description = "intraId와 같게 회원가입되어 변경 불가")
        @NotBlank
        private String oauth2Username;

        @Schema(name = "nickname" , example = "꿈꾸는 나무", description = "로그인 할 때 id가 아니라 사용자가 변경할 수 있는 이름, default는 oauth2Username와 같음.")
        @NotBlank
        private String nickname;

        @Schema(name = "email" , example = "takim@student.42seoul.kr", description = "intra email과 같게 회원가입됨.")
        @NotBlank
        private String email;

        @Schema(name = "imageUrl" , example = "https://cdn.intra.42.fr/users/medium_takim.jpg", description = "intra profile image와 같게 회원가입됨.")
        @NotBlank
        private String imageUrl;
//
//        @Schema(name = "slackEmail" , example="abccc@gmail.com", description = "slack 알림을 위해 사용될 수 있음. 아직은 사용하지 않음.")
//        private String slackEmail;

    }

}

