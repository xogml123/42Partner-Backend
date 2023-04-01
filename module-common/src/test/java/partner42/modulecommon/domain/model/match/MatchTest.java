package partner42.modulecommon.domain.model.match;


import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import partner42.modulecommon.domain.model.activity.Activity;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.Role;
import partner42.modulecommon.domain.model.user.RoleEnum;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.domain.model.user.UserRole;
import partner42.modulecommon.exception.BusinessException;
import partner42.modulecommon.exception.ErrorCode;

class MatchTest {

    @Test
    void makeReview() {
        Member takim = Member.of("takim");
        Member sorkim = Member.of("sorkim");
        UserRole.of(Role.of(RoleEnum.ROLE_USER), User.of(null, null, null, null, null, takim));

        Member notParticipated = Member.of("notParticipated");
        Match match = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.RANDOM,
            null, 3);
        MatchMember.of(match, takim, true);
        MatchMember.of(match, sorkim, false);
        //when
        List<Activity> activities = match.makeReview(takim,
            Map.of(sorkim, ActivityMatchScore.MATCH_REVIEW_1));
        //then
        assertThat(activities).extracting(Activity::getMember, Activity::getActivityMatchScore, Activity::getContentCategory)
            .containsExactlyInAnyOrder(
                tuple(takim, ActivityMatchScore.MAKE_MATCH_REVIEW, ContentCategory.MEAL),
                tuple(sorkim, ActivityMatchScore.MATCH_REVIEW_1, ContentCategory.MEAL)
            );
    }
    @Test
    void makeReview_whenNotParticipated_thenThrow() {
        Member takim = Member.of("takim");
        Member sorkim = Member.of("sorkim");
        Member notParticipated = Member.of("notParticipated");

        UserRole.of(Role.of(RoleEnum.ROLE_USER),
            User.of(null, null, null, null, null, notParticipated));

        Match match = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.RANDOM,
            null, 3);
        MatchMember.of(match, takim, true);
        MatchMember.of(match, sorkim, false);
        //then
        assertThatThrownBy(() ->
            match.makeReview(notParticipated,
                Map.of(sorkim, ActivityMatchScore.MATCH_REVIEW_1)))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    void makeReview_whenReviewedMemberIsMe_thenThrow() {
        Member takim = Member.of("takim");
        Member sorkim = Member.of("sorkim");

        UserRole.of(Role.of(RoleEnum.ROLE_USER),
            User.of(null, null, null, null, null, takim));

        Match match = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.RANDOM,
            null, 3);
        MatchMember.of(match, takim, true);
        MatchMember.of(match, sorkim, false);
        //then
        assertThatThrownBy(() ->
            match.makeReview(takim,
                Map.of(takim, ActivityMatchScore.MATCH_REVIEW_1)))
            .isInstanceOf(BusinessException.class)
            .hasMessage(ErrorCode.REVIEWING_SELF.getMessage());
    }

    @Test
    void makeReview_whenReviewedMemberNotInMatch_thenThrow() {
        Member takim = Member.of("takim");
        Member sorkim = Member.of("sorkim");
        Member notParticipated = Member.of("notParticipated");

        UserRole.of(Role.of(RoleEnum.ROLE_USER),
            User.of(null, null, null, null, null, takim));

        Match match = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.RANDOM,
            null, 3);
        MatchMember.of(match, takim, true);
        MatchMember.of(match, sorkim, false);
        //then
        assertThatThrownBy(() ->
            match.makeReview(takim,
                Map.of(notParticipated, ActivityMatchScore.MATCH_REVIEW_1)))
            .isInstanceOf(BusinessException.class)
            .hasMessage(ErrorCode.REVIEWED_MEMBER_NOT_IN_MATCH.getMessage());
    }

    @Test
    void verifyMemberParticipatedInMatchOrAdmin() {
        Member takim = Member.of("takim");
        Member sorkim = Member.of("sorkim");
        Member notParticipated = Member.of("notParticipated");

        UserRole.of(Role.of(RoleEnum.ROLE_USER),
            User.of(null, null, null, null, null, notParticipated));

        Match match = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.RANDOM,
            null, 3);
        MatchMember.of(match, takim, true);
        MatchMember.of(match, sorkim, false);
        //then
        assertThatThrownBy(() ->
            match.verifyMemberParticipatedInMatchOrAdmin(notParticipated))
                .isInstanceOf(BusinessException.class)
            .hasMessage(ErrorCode.NOT_MATCH_PARTICIPATED.getMessage());
    }

    @Test
    void isMemberReviewed() {
        Member takim = Member.of("takim");
        Member sorkim = Member.of("sorkim");
        Member notParticipated = Member.of("notParticipated");

        UserRole.of(Role.of(RoleEnum.ROLE_USER),
            User.of(null, null, null, null, null, takim));

        Match match = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.RANDOM,
            null, 3);
        MatchMember.of(match, takim, true);
        MatchMember.of(match, sorkim, false);
        match.makeReview(takim,
            Map.of(sorkim, ActivityMatchScore.MATCH_REVIEW_1));
        //then
        assertThat(match.isMemberReviewingBefore(takim)).isTrue();
        assertThat(match.isMemberReviewingBefore(sorkim)).isFalse();
    }
}