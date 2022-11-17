package partner42.moduleapi.dto.random;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import partner42.moduleapi.dto.matchcondition.MatchConditionRandomMatchDto;
import partner42.modulecommon.domain.model.match.ContentCategory;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomMatchDto {

    @Schema(name = "contentCategory", example = "MEAL or STUDY", description = "식사, 공부 글인지 여부")
    @NotNull
    private ContentCategory contentCategory;

    @Schema(name = "matchConditionRandomMatchDto", example = "", description = "매칭 조건")
    @NotNull
    private MatchConditionRandomMatchDto matchConditionRandomMatchDto;

}