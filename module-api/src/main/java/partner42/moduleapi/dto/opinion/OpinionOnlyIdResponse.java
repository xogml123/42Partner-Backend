package partner42.moduleapi.dto.opinion;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpinionOnlyIdResponse {
    @Schema(name = "opinionId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "댓글 ID")
    @NotBlank
    private String opinionId;
}