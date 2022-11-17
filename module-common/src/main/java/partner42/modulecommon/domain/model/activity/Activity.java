package partner42.modulecommon.domain.model.activity;

import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Table(name = "ACTIVITY")
@Entity
public class Activity extends BaseEntity {
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
    @Column(name = "ACTIVITY_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/



    @Column(nullable = false, updatable = false)
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ContentCategory contentCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ActivityType activityType;


    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false, updatable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_ID", nullable = false, updatable = false)
    private Match match;





    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    public static Activity of(Member member, Match match, Integer score, ContentCategory contentCategory,
        ActivityType activityType) {
        return Activity.builder()
            .member(member)
            .match(match)
            .score(score)
            .contentCategory(contentCategory)
            .activityType(activityType)
            .build();
    }


    /********************************* 비니지스 로직 *********************************/


    /********************************* DTO *********************************/
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ActivityScoreResponse {


        @Schema(name = "score", example = "1523", description = "활동 점수")
        @NotNull
        private Integer score;

        public static ActivityScoreResponse of(Integer score) {
            return ActivityScoreResponse.builder()
                .score(score)
                .build();
        }
    }


}


