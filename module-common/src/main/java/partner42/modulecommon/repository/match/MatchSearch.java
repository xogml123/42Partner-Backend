package partner42.modulecommon.repository.match;

import partner42.modulecommon.domain.model.match.ContentCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchSearch {
    @Schema(name = "contentCategory", example = "MEAL or STUDY", description = "식사, 공부 글인지 여부, 값을 꼭 지정해야함.")
    @NotNull
    private ContentCategory contentCategory;
}
