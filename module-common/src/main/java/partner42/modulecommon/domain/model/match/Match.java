package partner42.modulecommon.domain.model.match;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
import partner42.modulecommon.domain.model.activity.Activity;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.matchcondition.MatchConditionMatch;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.RoleEnum;
import partner42.modulecommon.exception.BusinessException;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;


@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table(name = "MATCHS", uniqueConstraints = {
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"}),
})

@Entity
public class Match extends BaseEntity {
    //********************************* static final 상수 필드 *********************************/


    private static final int REVIEW_AVAILABLE_MINUTE = 30;

    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATCH_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/

    /**
     * AUTH에 필요한 필드
     */

    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private MatchStatus matchStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ContentCategory contentCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private MethodCategory methodCategory;

    @Column(nullable = false)
    private Integer participantNum;

    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    /**
     * Article로 매칭이 맺어지는 경우
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID", updatable = false)
    private Article article;

    @Builder.Default
    @OneToMany(mappedBy = "match", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<MatchMember> matchMembers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "match", fetch = FetchType.LAZY, , cascade = CascadeType.PERSIST)
    private List<MatchConditionMatch> matchConditionMatches = new ArrayList<>();

    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    public static Match of(MatchStatus matchStatus, ContentCategory contentCategory,
        MethodCategory methodCategory, Article article, Integer participantNum) {

        return Match.builder()
            .matchStatus(matchStatus)
            .contentCategory(contentCategory)
            .participantNum(participantNum)
            .methodCategory(methodCategory)
            .article(article)
            .build();
    }

    /**
     * 30분 뒤에 매칭 리뷰 남길 수 있음.
     * @return
     */
    public LocalDateTime getReviewAvailableTime() {
        return this.getCreatedAt().plusMinutes(REVIEW_AVAILABLE_MINUTE);
    }

    /********************************* 비니지스 로직 *********************************/
    public List<Activity> makeReview(Member reviewer, Map<Member, ActivityMatchScore> reviewedMemberScoreMap) {

        verifyReviewerParticipatedInMatch(reviewer);
        verifyReviewedMemberInMatchAndNotReviewer(reviewer, reviewedMemberScoreMap.keySet());
        //리뷰 작성자 매칭 참여 여부 true로 변경
        memberIsReviewedToTrueIfParticipated(reviewer);

        List<Activity> activities = new ArrayList<>();
        //리뷰 작성자 참여 점수 추가.
        Activity reviewerActivity = Activity.of(reviewer,
            contentCategory,
            ActivityMatchScore.MAKE_MATCH_REVIEW);
        activities.add(reviewerActivity);

        // 리뷰에 따라 점수 추가
        reviewedMemberScoreMap.forEach((member, score) -> {
            Activity activity = Activity.of(member,
                contentCategory,
                score);
            activities.add(activity);
        });
        return activities;
    }
    /**
     * 자기가 참여한 매치인지 확인
     * @param reviewer
     */
    public void verifyReviewerParticipatedInMatch(Member reviewer) {
        if (reviewer.getUser().hasRole(RoleEnum.ROLE_ADMIN)){
            return ;
        }
        if (!matchMembers.stream()
                .map(MatchMember::getMember)
                .collect(Collectors.toSet())
                .contains(reviewer)) {
            throw new BusinessException(ErrorCode.NOT_MATCH_PARTICIPATED);
        }
    }

    public Boolean isMemberReviewed(Member member){
        return getMatchMembers().stream()
            .filter((mm) ->
                member.equals(mm.getMember())
            ).findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException("해당 매치에 참여한 멤버가 아닙니다."))
            .getIsReviewed();
    }
    private void memberIsReviewedToTrueIfParticipated(Member member){
        matchMembers.stream()
            .filter(mm ->
                mm.getMember().equals(member))
            .findAny()
            .orElseThrow(() ->
                new IllegalArgumentException("해당 매치에 참여한 멤버가 아닙니다."))
            .updateisReviewedToTrue();
    }

    /**
     * 매칭 대상자가 자기 자신이 아니고 매칭에 포함되어있는지 확인
     * @param reviewer
     * @param reviewedMemberSet
     */
    private void verifyReviewedMemberInMatchAndNotReviewer(Member reviewer, Set<Member> reviewedMemberSet) {

        Set<Member> memberSet = this.getMatchMembers()
            .stream()
            .map(MatchMember::getMember)
            .collect(Collectors.toSet());

        reviewedMemberSet
            .forEach((member) -> {
                if (!memberSet.contains(member)) {
                    throw new BusinessException(ErrorCode.REVIEWED_MEMBER_NOT_IN_MATCH);
                }else if(reviewer.equals(member)){
                    throw new BusinessException(ErrorCode.REVIEWING_SELF);
                }
            });
    }
}

