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

//    /********************************* 랜덤 매칭 키를 만들기 위한 상수들 *********************************/
//
//    /**
//     * TypeOfStudy, Place와 같은 RandomMatch의 조건이 될 enum 들은 name() 길이가 20을 넘어서는 안됨. enum값을 활용하여 큰 정수값으로
//     * 변환하면 비교알고리즘에서 더 높은 성능을 기대할 수 있지만, enum에 값의 종류를 추가할 경우 기존의 enum값들의 정수값이 변경되어 비교알고리즘에 문제가 생길 수
//     * 있음. 물론 조심 하면 될 부분일 수도 있고 redis에 저장한 데이터의 만료시간이 30분이므로 이 안에만 enum에 값이 추가 되고 바로 배포되는 경우가 아니라면 큰
//     * 문제는 없을 수도 있음. 하지만, 숫자로 변경해서 구현 시 redis에서 어떤 매칭이 이루어지고 있는지 확인할 수 없음. 또한
//     */
//    protected static final Integer STRING_CONDITION_MAX_LENGTH = 20;
//    /**
//     * 100000가지 조건 표현 가능.
//     */
//    protected static final Integer INTEGER_CONDITION_MAX_LENGTH = 5;
//    protected static final Integer ASCII_RANGE = 256;
//
//    /**
//     * "-"가 모든 숫자, 알파벳 보다 아스키 코드값이 더 작으므로 오름 차순 정렬에서 더 긴 문자열이 더 앞에 위치함.
//     */
//    protected static final String CONDITION_PAD_CHAR = "-";
//    protected static final String ID_DELIMITER = "/";
//
//    public static final List<String> MEAL_CONDITION_LIST = new ArrayList<>();
//    public static final List<String> STUDY_CONDITION_LIST = new ArrayList<>();
//
//
//

//
//
//    /**
//     * 매칭 방이 될 수 있는 목록.
//     */
//    static {
//        for (ContentCategory contentCategory : ContentCategory.values()) {
//            String s0 = StringUtils.rightPad(contentCategory.name(),
//                RandomMatch.STRING_CONDITION_MAX_LENGTH,
//                RandomMatch.CONDITION_PAD_CHAR);
//            for (Place place : Place.values()) {
//                String s1 = s0 + StringUtils.rightPad(place.name(),
//                    RandomMatch.STRING_CONDITION_MAX_LENGTH,
//                    RandomMatch.CONDITION_PAD_CHAR);
//                if (contentCategory == ContentCategory.MEAL) {
//                    for (WayOfEating wayOfEating : WayOfEating.values()) {
//                        String s2 = s1 + StringUtils.rightPad(wayOfEating.name(),
//                            RandomMatch.STRING_CONDITION_MAX_LENGTH,
//                            RandomMatch.CONDITION_PAD_CHAR);
//                        MEAL_CONDITION_LIST.add(s2);
//                    }
//                } else if (contentCategory == ContentCategory.STUDY) {
//                    for (TypeOfStudy typeOfStudy : TypeOfStudy.values()) {
//                        String s2 = s1 + StringUtils.rightPad(typeOfStudy.name(),
//                            RandomMatch.STRING_CONDITION_MAX_LENGTH,
//                            RandomMatch.CONDITION_PAD_CHAR);
//                        STUDY_CONDITION_LIST.add(s2);
//                    }
//                }
//            }
//        }
//    }


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

//
//    public abstract String toStringKey();
//
//    public abstract String toKey();
//
//    public abstract String toNumberKey();
//
//    public abstract String toAsciiKey();
//
//    /**
//     * 같은 조건일 경우 userId크기에 따라 특정 유저가 항상 우선순위에 앞서거나 뒤쳐지기 때문에 랜덤한 문자열을 추가함. 30분마다 사라지는 데이터이므로 3바이트 크기의
//     * 문자열이면 충분함.
//     */
//    public String toValue() {
//        return createdAt +
//            ID_DELIMITER +
//            member.getId().toString();
//    }
//

//
//    public static String[] splitApplyingInfo(String applyingInfo) {
//        return applyingInfo.split(RandomMatch.ID_DELIMITER);
//    }
//
//    public static String[] splitKeyInfo(String key) {
//        return key.split(RandomMatch.CONDITION_PAD_CHAR);
//    }



}
