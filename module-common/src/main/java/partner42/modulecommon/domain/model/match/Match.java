package partner42.modulecommon.domain.model.match;


import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchConditionMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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

    /**
     * email 뒤에 붙는 문자열
     */


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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID", updatable = false)
    private Article article;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "match")
    @Column(nullable = false, updatable = false)
    private List<MatchMember> matchMembers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "match", fetch = FetchType.LAZY)
    private List<MatchConditionMatch> matchConditionMatches = new ArrayList<>();

    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    public static Match of(MatchStatus matchStatus, ContentCategory contentCategory, MethodCategory methodCategory, Article article, Integer participantNum) {
        return Match.builder()
            .matchStatus(matchStatus)
            .contentCategory(contentCategory)
            .participantNum(participantNum)
            .methodCategory(methodCategory)
            .article(article)
            .build();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MatchDto {


        @Schema(name = "matchId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "매치 ID")
        @NotBlank
        @Size(min = 1, max = 100)
        private String matchId;

        @Schema(name = "createdAt", example = "2022-10-03T00:00:00", description = "작성 시간")
        private LocalDateTime createdAt;

        @Schema(name = "matchStatus", example = "MATCHED(\"매칭 완료\"), CANCELED(\"취소\");", description = " 매칭 완료, 취소 여부(현 상황에서는 매칭 완료 인것만 보내짐.)")
        @NotNull
        private MatchStatus matchStatus;

        @Schema(name = "contentCategory", example = "MEAL or STUDY", description = "식사, 공부 글인지 여부")
        @NotNull
        private ContentCategory contentCategory;

        @Schema(name = "methodCategory", example = "RANDOM, MANUAL", description = "랜덤, 인지 방매칭인지 여부")
        @NotNull
        private MethodCategory methodCategory;

        @Schema(name = "participantNum", example = "4", description = "현재 방에 참여중인 인원")
        @NotNull
        private Integer participantNum;

        @Schema(name = "matchConditionDto", example = "", description = "매칭 조건")
        @NotNull
        private MatchCondition.MatchConditionDto matchConditionDto;

        public static MatchDto of(Match match, MatchCondition.MatchConditionDto matchConditionDto) {
            return MatchDto.builder()
                .matchId(match.getApiId())
                .createdAt(match.getCreatedAt())
                .matchStatus(match.getMatchStatus())
                .contentCategory(match.getContentCategory())
                .methodCategory(match.getMethodCategory())
                .participantNum(match.getParticipantNum())
                .matchConditionDto(matchConditionDto)
                .build();
        }

    }

    /********************************* 비니지스 로직 *********************************/

}

