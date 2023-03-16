package partner42.moduleapi.service.match;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.match.MatchDto;
import partner42.moduleapi.dto.match.MatchReviewRequest;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.dto.member.MemberReviewDto;
import partner42.moduleapi.mapper.MemberMapper;
import partner42.modulecommon.domain.model.activity.Activity;
import partner42.modulecommon.domain.model.activity.ActivityMatchScore;
import partner42.modulecommon.domain.model.activity.ActivityType;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TimeOfEating;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.Role;
import partner42.modulecommon.domain.model.user.RoleEnum;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.domain.model.user.UserRole;
import partner42.modulecommon.exception.BusinessException;
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
                    match.getMatchMembers().stream()
                        .filter((matchMember) ->
                            member.equals(matchMember.getMember())
                        ).findFirst()
                        .orElseThrow(() ->
                            new NoEntityException(ErrorCode.ENTITY_NOT_FOUND))
                        .getIsReviewed()
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
        verifyNotMatchParticipated(username, matchId);

        Member member = getUserByUsernameOrException(username)
            .getMember();

        Match match = matchRepository.findByApiId(matchId)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
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
            match.getMatchMembers().stream()
                .filter((matchMember) ->
                    member.equals(matchMember.getMember())
                ).findFirst()
                .orElseThrow(() ->
                    new NoEntityException(ErrorCode.ENTITY_NOT_FOUND))
                .getIsReviewed()
        );
    }


    public ResponseEntity<Void> makeReview(String username, String matchId,
        MatchReviewRequest request) {
        verifyNotMatchParticipated(username, matchId);

        verifyReviewedMemberNotInMatch(matchId, request.getMemberReviewDtos().stream()
            .map(MemberReviewDto::getNickname)
            .collect(Collectors.toList()));

        Match match = matchRepository.findByApiId(matchId)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        //uesrname에 해당하는 matchMember의 매칭상태 true로 변경.
        match.getMatchMembers().stream()
            .filter(mm ->
                mm.getMember().getUser().getUsername().equals(username))
            .findAny()
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND))
            .updateReviewStatusTrue();
        //리뷰 작성자 참여 점수 추가.
        Member memberReviewAuthor = memberRepository.findByNickname(username)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        activityRepository.save(
            Activity.of(memberReviewAuthor,
                match.getContentCategory(),
                ActivityMatchScore.MATCH_REVIEW));
        /**
         * 리뷰에 따라 점수 추가
         */
        List<Activity> activities = request.getMemberReviewDtos().stream()
            .map((memberReviewDto) -> {
                Member member = memberRepository.findByNickname(memberReviewDto.getNickname())
                    .orElseThrow(() ->
                        new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
                return Activity.of(member, match.getContentCategory(),
                    memberReviewDto.getActivityMatchScore());
            })
            .collect(Collectors.toList());
        activityRepository.saveAll(activities);
        return ResponseEntity.ok().build();
    }

    private void verifyReviewedMemberNotInMatch(String matchId, List<String> nicknames) {
        Match match = matchRepository.findByApiId(matchId)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        Set<Member> memberSet = match.getMatchMembers()
            .stream()
            .map(MatchMember::getMember)
            .collect(Collectors.toSet());
        memberRepository.findAllByNicknameIn(nicknames)
            .forEach((member) -> {
                if (!memberSet.contains(member)) {
                    throw new BusinessException(ErrorCode.REVIEWED_MEMBER_NOT_IN_MATCH);
                }
            });

    }

    //자기 매치인지 확인
    private void verifyNotMatchParticipated(String username, String matchId) {

        User user = getUserByUsernameOrException(username);
        Match match = matchRepository.findByApiId(matchId)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
        if (!user.getUserRoles().stream()
            .map(UserRole::getRole)
            .map(Role::getValue)
            .collect(Collectors.toSet())
            .contains(RoleEnum.ROLE_ADMIN) &&
            !match.getMatchMembers().stream()
                .map(MatchMember::getMember)
                .collect(Collectors.toSet())
                .contains(user.getMember())) {
            throw new BusinessException(ErrorCode.NOT_MATCH_PARTICIPATED);
        }
    }
}
