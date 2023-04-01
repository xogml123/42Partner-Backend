package partner42.moduleapi.service.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.match.MatchDto;
import partner42.moduleapi.dto.match.MatchReviewRequest;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.dto.member.MemberReviewDto;
import partner42.moduleapi.mapper.MemberMapper;
import partner42.modulecommon.domain.model.activity.Activity;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TimeOfEating;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.repository.activity.ActivityRepository;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.match.MatchSearch;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.user.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    private final MemberRepository memberRepository;
    private final ActivityRepository activityRepository;

    private final MemberMapper memberMapper;

    public SliceImpl<MatchDto> readMyMatches(String username, MatchSearch matchSearch,
        Pageable pageable) {
        Member member = getUserByUsernameOrException(username)
            .getMember();
        Slice<Match> matchSlices = matchRepository.findAllMatchByMemberIdAndByMatchSearch(
            member.getId(), matchSearch, pageable);
        List<MatchDto> content = matchSlices
            .getContent()
            .stream()
            .map((match) -> {
                List<MatchCondition> matchConditions = match.getMatchConditionMatches().stream()
                    .map((matchConditionMatch) ->
                        matchConditionMatch.getMatchCondition()
                    )
                    .collect(Collectors.toList());

                return MatchDto.of(match, MatchConditionDto.of(
                        Place.extractPlaceFromMatchCondition(matchConditions),
                        TimeOfEating.extractTimeOfEatingFromMatchCondition(matchConditions),
                        WayOfEating.extractWayOfEatingFromMatchCondition(matchConditions),
                        TypeOfStudy.extractTypeOfStudyFromMatchCondition(matchConditions)
                    ), match.getMatchMembers().stream()
                        .map((matchMember) ->
                            memberMapper.matchMemberToMemberDto(matchMember.getMember(), matchMember,
                                member.equals(matchMember.getMember()))
                        )
                        .collect(Collectors.toList()),
                    match.isMemberReviewingBefore(member)
                );
            })
            .collect(Collectors.toList());

        return new SliceImpl<MatchDto>(content, matchSlices.getPageable(), matchSlices.hasNext());
    }

    private User getUserByUsernameOrException(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
    }

    public MatchDto readOneMatch(String username, String matchId) {
        //자기 매치인지 확인
        Member member = getUserByUsernameOrException(username).getMember();
        Match match = matchRepository.findByApiId(matchId)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        match.verifyMemberParticipatedInMatchOrAdmin(member);

        List<MatchCondition> matchConditions = match.getMatchConditionMatches().stream()
            .map((matchConditionMatch) ->
                matchConditionMatch.getMatchCondition()
            )
            .collect(Collectors.toList());
        return MatchDto.of(match, MatchConditionDto.of(
                Place.extractPlaceFromMatchCondition(matchConditions),
                TimeOfEating.extractTimeOfEatingFromMatchCondition(matchConditions),
                WayOfEating.extractWayOfEatingFromMatchCondition(matchConditions),
                TypeOfStudy.extractTypeOfStudyFromMatchCondition(matchConditions)),
            match.getMatchMembers().stream()
                .map((matchMember) ->
                    memberMapper.matchMemberToMemberDto(matchMember.getMember(), matchMember,
                        member.equals(matchMember.getMember()))
                )
                .collect(Collectors.toList()),
            match.isMemberReviewingBefore(member)
        );
    }


    @Transactional
    public List<Activity> makeReview(String username, String matchId,
        MatchReviewRequest request) {
        Match match = matchRepository.findByApiId(matchId)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        User reviewingUser = getUserByUsernameOrException(username);
        Map<String, ActivityMatchScore> nicknameActivityScoreMap = request.getMemberReviewDtos().stream()
            .collect(Collectors.toMap(MemberReviewDto::getNickname,
                MemberReviewDto::getActivityMatchScore));
        List<Member> reviewedTargets = memberRepository.findAllByNicknameIn(
            new ArrayList<>(nicknameActivityScoreMap.keySet()));
        // request의 nickname이 member에 없는 경우
        if (reviewedTargets.size() != nicknameActivityScoreMap.size()) {
            throw new NoEntityException(ErrorCode.ENTITY_NOT_FOUND);
        }
        List<Activity> createdActivities = match.makeReview(reviewingUser.getMember(),
            reviewedTargets.stream()
                .collect(Collectors.toMap(m -> m,
                    m -> nicknameActivityScoreMap.get(m.getNickname()))));
        return activityRepository.saveAll(createdActivities);
    }

}
