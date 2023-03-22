package partner42.modulecommon.domain.model.random;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.InvalidInputException;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "RANDOM_MATCHES"
    , indexes = @Index(name = "idx__created_at", columnList = "createdAt")
)
public class RandomMatch extends BaseEntity implements Serializable {

    //
    private static final long serialVersionUID = 1L;

    //매칭 maximum대기 시간
    public static final Integer MAX_WAITING_TIME = 30;
    public static final Integer MATCH_COUNT = 2;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RANDOM_MATCH_ID")
    private Long id;

    @Version
    private Long version;

    @Embedded
    private RandomMatchCondition randomMatchCondition;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isExpired = false;

    /********************************* 연관관계 매핑 *********************************/
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

    /********************************* 생성 메서드 *********************************/
    public static RandomMatch of(RandomMatchCondition randomMatchCondition, Member member) {
        return RandomMatch.builder()
            .randomMatchCondition(randomMatchCondition)
            .member(member)
            .build();
    }

    /********************************* 비지니스 로직 *********************************/

    public boolean isMatchConditionEquals(RandomMatch randomMatch) {
        return this.randomMatchCondition.equals(randomMatch.getRandomMatchCondition());
    }
    public void expire() {
        verifyExpire();
        this.isExpired = true;
    }

    private void verifyExpire() {
        if (this.isExpired) {
            throw new InvalidInputException(ErrorCode.ALREADY_CANCELED_RANDOM_MATCH);
        }
    }

    /********************************* DTO *********************************/


    /********************************* Comparator *********************************/
    public static class MatchConditionComparator implements Comparator<RandomMatch> {
        @Override
        public int compare(RandomMatch o1, RandomMatch o2) {
            return o1.getRandomMatchCondition().compareTo(o2.getRandomMatchCondition());
        }
    }
    /********************************* 비지니스 로직 *********************************/
    public static LocalDateTime getValidTime(LocalDateTime now) {
        return now.minusMinutes(RandomMatch.MAX_WAITING_TIME);
    }
}
