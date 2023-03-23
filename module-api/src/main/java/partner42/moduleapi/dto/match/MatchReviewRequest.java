package partner42.moduleapi.dto.match;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import partner42.moduleapi.dto.member.MemberReviewDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchReviewRequest {

    @Schema(name = "matchId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "매치 ID")
    @NotBlank
    @Size(min = 1, max = 100)
    private String matchId;

    @Schema(name = "memberReviewDtos", example = "[]", description = "멤버별 리뷰 남길 정보")
    @Builder.Default
    private List<MemberReviewDto> memberReviewDtos = new ArrayList<>();
}
