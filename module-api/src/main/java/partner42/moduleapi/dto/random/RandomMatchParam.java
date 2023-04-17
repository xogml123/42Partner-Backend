package partner42.moduleapi.dto.random;


import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import partner42.modulecommon.domain.model.match.ContentCategory;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomMatchParam {
    @Schema(description = "확인할 랜덤 매칭의 카테고리", example = "MEAL or STUDY")
    @NotNull
    private ContentCategory contentCategory;
}
