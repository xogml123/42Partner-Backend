package partner42.modulecommon.domain.model.alarm;


import com.vladmihalcea.hibernate.type.json.JsonType;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.member.Member;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "ALARM", uniqueConstraints = {
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"})
})
@TypeDef(name = "json", typeClass = JsonType.class)
@Entity
public class Alarm extends BaseEntity {


    //********************************* static final 상수 필드 *********************************/


    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ALARM_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/

    /**
     * AUTH에 필요한 필드
     */

    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmType alarmType;

    @Builder.Default
    @Column(nullable = false)
    private boolean isRead = false;

    /**
     * 알람 기능이 확장 될 때 필요한 정보들을 저장.
     * json 형태로 저장하면 각 정보를 Column으로 저장하는 것 보다
     * 여러 알림 형태에 대응하기 좋음.
     * ex) 이벤트가 발생한 글 정보를 저장하여 링크 클릭 시 그 글로 이동할 수 있도록 함.
     */
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private AlarmArgs alarmArgs;

    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    /**
     * 알람을 받는 사람
     */
    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "CALLED_MEMBER_ID", nullable = false, updatable = false)
    private Member calledMember;

    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/
    public static Alarm of(AlarmType alarmType, AlarmArgs alarmArgs, Member calledMember) {
        Alarm alarm = Alarm.builder()
            .alarmType(alarmType)
            .alarmArgs(alarmArgs)
            .build();
        alarm.setCalledMember(calledMember);
        return alarm;
    }



    /********************************* 비니지스 로직 *********************************/
    public void read() {
        if (!this.isRead) {
            this.isRead = true;
        }
    }
    private void setCalledMember(Member calledMember) {
        this.calledMember = calledMember;
        calledMember.getAlarms().add(this);
    }



}
