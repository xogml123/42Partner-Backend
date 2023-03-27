package partner42.modulecommon.domain.model.member;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.alarm.Alarm;
import partner42.modulecommon.domain.model.user.Role;
import partner42.modulecommon.domain.model.user.RoleEnum;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.domain.model.user.UserRole;


@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "MEMBER", uniqueConstraints = {
    @UniqueConstraint(name = "NICK_NAME_UNIQUE", columnNames = {"nickname"}),
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"})
})
@Entity
public class Member extends BaseEntity {
    //********************************* static final 상수 필드 *********************************/

    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }
    /********************************* PK가 아닌 필드 *********************************/

    /**
     * AUTH에 필요한 필드
     */

    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();

    @Column(unique = true, nullable = false, length = 30)
    private String nickname;
    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

//    FetchType.LAZY가 실질적으로 적용안됨 항상 EAGER로 적용
//    @OneToOne(fetch = FetchType.LAZY, mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
//    private MatchTryAvailabilityJudge matchTryAvailabilityJudge;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "member")
    private User user;

    /**
     * 내가 알림을 받아야하는 경우의 알림 목록.
     */
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "calledMember")
    private List<Alarm> alarms = new ArrayList<>();
    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/
    public static Member of(String nickname) {
        Member member = Member.builder()
            .nickname(nickname)
            .build();
        return member;
    }

    /********************************* 비니지스 로직 *********************************/
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
