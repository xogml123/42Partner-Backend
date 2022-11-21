package partner42.moduleapi.service.match;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.match.MatchDto;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TimeOfEating;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.match.MatchSearch;
import partner42.modulecommon.repository.user.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;


    public Slice<MatchDto> readMyMatches(String userId, MatchSearch matchSearch,
        Pageable pageable) {
        Member member = userRepository.findByApiId(userId)
            .orElseThrow(() ->
                new NoEntityException(ErrorCode.ENTITY_NOT_FOUND))
            .getMember();
        return matchRepository.findAllFetchJoinMatchMemberId(member.getId(), matchSearch, pageable)
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
                    TypeOfStudy.extractTypeOfStudyFromMatchCondition(matchConditions)));
            });

    }

    public MatchDto readOneMatch(String apiId, String matchId) {
        //자기 매치인지 확인

        Match match = matchRepository.findByApiId(apiId)
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
            TypeOfStudy.extractTypeOfStudyFromMatchCondition(matchConditions)));

    }
}
