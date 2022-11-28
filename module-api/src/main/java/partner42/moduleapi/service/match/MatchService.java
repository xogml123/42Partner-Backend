package partner42.moduleapi.service.match;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.match.MatchDto;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.dto.member.MemberDto;
import partner42.moduleapi.mapper.MemberMapper;
import partner42.modulecommon.domain.model.article.Article;
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
import partner42.modulecommon.exception.NotAuthorException;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.match.MatchSearch;
import partner42.modulecommon.repository.user.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    private final MemberMapper memberMapper;

    public SliceImpl<MatchDto> readMyMatches(String username, MatchSearch matchSearch,
        Pageable pageable) {
        Member member = userRepository.findByUsername(username)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND))
            .getMember();
        Slice<Match> matchSlices = matchRepository.findAllFetchJoinMatchMemberId(
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
                        .collect(Collectors.toList())
                );
            })
            .collect(Collectors.toList());

        return new SliceImpl<MatchDto>(content, matchSlices.getPageable(), matchSlices.hasNext());
    }

    public MatchDto readOneMatch(String username, String matchId) {
        //자기 매치인지 확인
        Member member = userRepository.findByUsername(username)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND))
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
                .collect(Collectors.toList()));
    }

    //자기 매치인지 확인
    private verifyNotMatchParticipated(String username, String matchId) {

        User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND));
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
