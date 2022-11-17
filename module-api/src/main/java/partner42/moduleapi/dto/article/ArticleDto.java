package partner42.moduleapi.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
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
import partner42.modulecommon.domain.model.match.ContentCategory;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleDto {


    @Schema(name = "title", example = "개포에서 2시에 점심 먹으실 분 구합니다.", description = "글 제목")
    @NotBlank
    @Size(min = 1, max = 255)
    private String title;

    @Schema(name = "date", example = "2022-10-03", description = "식사 날짜")
    @NotNull
    private LocalDate date;

    @Schema(name = "matchConditionDto", example = "", description = "매칭 조건")
    @NotNull
    private MatchConditionDto matchConditionDto;

    @Schema(name = "content", example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "글 본문")
    @NotNull
    @Size(min = 1, max = 100000)
    private String content;

    @Schema(name = "anonymity", example = "true", description = "익명 여부")
    @NotNull
    private Boolean anonymity;

    @Schema(name = "participantNumMAx", example = "5", description = "방 최대 참여자 수")
    @NotNull
    @Min(1)
    @Max(20)
    private Integer participantNumMax;

    @Schema(name = "contentCategory", example = "MEAL or STUDY", description = "식사, 공부 글인지 여부, 글 내용 변경 시 공부, 식사 카테고리를 변경할지는 클라이언트에서 결정")
    @NotNull
    private ContentCategory contentCategory;
}
