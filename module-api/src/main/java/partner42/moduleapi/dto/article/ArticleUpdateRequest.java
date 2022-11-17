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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleUpdateRequest {

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


    @Schema(name = "participantNumMAx", example = "5", description = "방 최대 참여자 수")
    @NotNull
    @Min(2)
    @Max(20)
    private Integer participantNumMax;
}
