package partner42.moduleapi.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberReviewDto {

    @Schema(name = "nickname", example = "takim", description = "매치 ID")
    @NotBlank
    @Size(min = 1, max = 100)
    private String nickname;

    @Schema(name = "activityMatchScore", example = "MATCH_REVIEW_1", description = "MATCH_REVIEW_1(\"매치 리뷰 1점\"),\n"
        + "    MATCH_REVIEW_2(\"매치 리뷰 2점\"),\n"
        + "    MATCH_REVIEW_3(\"매치 리뷰 3점\"),\n"
        + "    MATCH_REVIEW_4(\"매치 리뷰 4점\"),\n"
        + "    MATCH_REVIEW_5(\"매치 리뷰 5점\"),\n"
        + "    MATCH_ABSENT(\"매치 불참\"),")
    @NotNull
    private ActivityMatchScore activityMatchScore;


}
