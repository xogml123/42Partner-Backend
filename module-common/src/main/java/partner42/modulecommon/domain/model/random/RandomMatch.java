package partner42.modulecommon.domain.model.random;


import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.InvalidInputException;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Table(name = "RANDOM_MATCHES"
    , indexes = @Index(name = "idx__created_at", columnList = "createdAt")
)
public abstract class RandomMatch extends BaseEntity implements Serializable {

    //
    private static final long serialVersionUID = 1L;

    //매칭 maximum대기 시간
    public static final Integer MAX_WAITING_TIME = 30;
    public static final Integer MATCH_COUNT = 2;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RANDOM_MATCH_ID")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    protected ContentCategory contentCategory;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    protected Place place;

    @Column(nullable = false)
    protected Boolean isExpired = false;

    /********************************* 연관관계 매핑 *********************************/
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_ID", updatable = false)
    private Match match;

    protected RandomMatch(ContentCategory contentCategory, Place place,
        Member member) {
        this.contentCategory = contentCategory;
        this.place = place;
        this.member = member;
    }

    /********************************* 비지니스 로직 *********************************/

    protected boolean isMatchConditionEquals(RandomMatch randomMatch) {
        return this.contentCategory == randomMatch.contentCategory &&
            this.place == randomMatch.place;
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

    public void updateMatch(Match match) {
        this.match = match;
        match.getRandomMatches().add(this);
    }

    /********************************* DTO *********************************/

}
