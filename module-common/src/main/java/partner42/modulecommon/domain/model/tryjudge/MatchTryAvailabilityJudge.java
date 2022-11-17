package partner42.modulecommon.domain.model.tryjudge;



import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.member.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "TRY_JUDGE")
@Entity
public class MatchTryAvailabilityJudge extends BaseEntity {
    //********************************* static final 상수 필드 *********************************/

    /**
     * email 뒤에 붙는 문자열
     */
    private static final Integer MEAL_MAX = 1;
    private static final Integer STUDY_MAX = 1;



    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRY_JUDGE_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/


    //내가 작성하고 있는 Aritcle의 개수를 감시하기 위해 필요
    @Builder.Default
    @Column(nullable = false)
    private Integer writingMealArticleCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer participatingMealArticleCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer randomMealCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer randomStudyArticleCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer writingStudyArticleCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer participatingStudyArticleCount = 0;





    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;


    /********************************* 생성 메서드 *********************************/
    public static MatchTryAvailabilityJudge of() {
        return MatchTryAvailabilityJudge.builder().build();
    }


    /********************************* 비니지스 로직 *********************************/
    public void setMember(Member member) {
        this.member = member;
        member.setMatchTryAvailabilityJudge(this);
    }
}

