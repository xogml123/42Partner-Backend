package partner42.moduleapi.dto.match;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchStatus;
import partner42.modulecommon.domain.model.match.MethodCategory;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchDto {


    @Schema(name = "matchId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "매치 ID")
    @NotBlank
    @Size(min = 1, max = 100)
    private String matchId;

    @NotNull
    @Schema(name = "createdAt", example = "2022-10-03T00:00:00", description = "매칭이 이루어진 시간")
    private LocalDateTime createdAt;

    @NotNull
    @Schema(name = "reviewAvailableTime", example = "2022-10-03T00:00:00", description = "이 시간 이후로 리뷰를 남길 수 있음.")
    private LocalDateTime reviewAvailableTime;

    @NotNull
    @Schema(name = "isReviewed", example = "true", description = "리뷰가 이루어졌는지 여부")
    private Boolean isReviewed;

    @Schema(name = "matchStatus", example = "MATCHED(\"매칭 완료\"), CANCELED(\"취소\");", description = " 매칭 완료, 취소 여부(현 상황에서는 매칭 완료 인것만 보내짐.)")
    @NotNull
    private MatchStatus matchStatus;

    @Schema(name = "contentCategory", example = "MEAL or STUDY", description = "식사, 공부 글인지 여부")
    @NotNull
    private ContentCategory contentCategory;

    @Schema(name = "methodCategory", example = "RANDOM, MANUAL", description = "랜덤, 인지 방매칭인지 여부")
    @NotNull
    private MethodCategory methodCategory;

    @Schema(name = "participantNum", example = "4", description = "현재 방에 참여중인 인원")
    @NotNull
    private Integer participantNum;

    @Schema(name = "matchConditionDto", example = "", description = "매칭 조건")
    @NotNull
    private MatchConditionDto matchConditionDto;

    @Builder.Default
    @Schema(name = "participantsOrAuthor", example = "[]", description = "방을 만든사람, 혹은 참여자가 담긴 배열")
    private List<MemberDto> participantsOrAuthor = new ArrayList<>();

    public static MatchDto of(Match match, MatchConditionDto matchConditionDto,
        List<MemberDto> participantsOrAuthor, boolean isReviewed) {

        return MatchDto.builder()
            .matchId(match.getApiId())
            .createdAt(match.getCreatedAt())
            .reviewAvailableTime(match.getReviewAvailableTime())
            .isReviewed(isReviewed)
            .matchStatus(match.getMatchStatus())
            .contentCategory(match.getContentCategory())
            .methodCategory(match.getMethodCategory())
            .participantNum(match.getParticipantNum())
            .matchConditionDto(matchConditionDto)
            .participantsOrAuthor(participantsOrAuthor)
            .build();
    }

}