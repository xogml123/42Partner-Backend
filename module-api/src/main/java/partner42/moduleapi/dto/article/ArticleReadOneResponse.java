package partner42.moduleapi.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.dto.member.MemberDto;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.match.ContentCategory;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleReadOneResponse {


    @Schema(name = "articleId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "게시글 ID")
    @NotBlank
    @Size(min = 1, max = 100)
    private String articleId;

    @Schema(name = "userId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "작성자 ID")
    @NotBlank
    @Size(min = 1, max = 100)
    private String userId;

    @Schema(name = "title", example = "개포에서 2시에 점심 먹으실 분 구합니다.", description = "글 제목")
    @NotBlank
    @Size(min = 1, max = 255)
    private String title;

    @Schema(name = "date", example = "2022-10-03", description = "식사 날짜")
    @NotNull
    private LocalDate date;

    @Schema(name = "createdAt", example = "2022-10-03 00:00:00", description = "작성 시간")
    private LocalDateTime createdAt;

    @Schema(name = "content", example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "글 본문")
    @NotNull
    @Size(min = 1, max = 100000)
    private String content;

    @Schema(name = "anonymity", example = "true", description = "익명 여부")
    @NotNull
    private Boolean anonymity;

    @Schema(name = "isToday", example = "true", description = "당일 여부")
    @NotNull
    private Boolean isToday;

    @Schema(name = "participantNumMax", example = "5", description = "방 최대 참여자 수")
    @NotNull
    @Min(1)
    @Max(20)
    private Integer participantNumMax;

    @Schema(name = "participantNum", example = " 2", description = "현재 방에 참여중인 인원")
    @NotNull
    private Integer participantNum;

    @Schema(name = "contentCategory", example = "MEAL or STUDY", description = "식사, 공부 글인지 여부")
    @NotNull
    private ContentCategory contentCategory;


    @Schema(name = "matchConditionDto", example = "", description = "매칭 조건")
    @NotNull
    private MatchConditionDto matchConditionDto;

    @Builder.Default
    @Schema(name = "participantsOrAuthor", example = " ", description = "방을 만든사람, 혹은 참여자가 담긴 배열")
    private List<MemberDto> participantsOrAuthor = new ArrayList<>();

    public static ArticleReadOneResponse of(Article article, List<MemberDto> memberDtos,
        MatchConditionDto matchConditionDto) {
        return ArticleReadOneResponse.builder()
            .articleId(article.getApiId())
            .userId(article.getAuthorMember().getUser().getApiId())
            .title(article.getTitle())
            .date(article.getDate())
            .createdAt(article.getCreatedAt())
            .content(article.getContent())
            .anonymity(article.getAnonymity())
            .isToday(article.isDateToday())
            .participantNumMax(article.getParticipantNumMax())
            .participantNum(article.getParticipantNum())
            .contentCategory(article.getContentCategory())
            .participantsOrAuthor(memberDtos)
            .matchConditionDto(matchConditionDto)
            .build();
    }
}