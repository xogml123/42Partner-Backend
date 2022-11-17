package partner42.moduleapi.dto.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
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
public class ActivityScoreResponse {


    @Schema(name = "score", example = "1523", description = "활동 점수")
    @NotNull
    private Integer score;

    public static ActivityScoreResponse of(Integer score) {
        return ActivityScoreResponse.builder()
            .score(score)
            .build();
    }
}
