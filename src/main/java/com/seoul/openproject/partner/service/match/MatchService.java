package com.seoul.openproject.partner.service.match;

import com.seoul.openproject.partner.domain.model.match.Match.MatchDto;
import com.seoul.openproject.partner.domain.model.matchcondition.MatchCondition;
import com.seoul.openproject.partner.domain.model.matchcondition.MatchCondition.MatchConditionDto;
import com.seoul.openproject.partner.domain.model.matchcondition.Place;
import com.seoul.openproject.partner.domain.model.matchcondition.TimeOfEating;
import com.seoul.openproject.partner.domain.model.matchcondition.TypeOfStudy;
import com.seoul.openproject.partner.domain.model.matchcondition.WayOfEating;
import com.seoul.openproject.partner.repository.match.MatchSearch;
import com.seoul.openproject.partner.domain.model.member.Member;
import com.seoul.openproject.partner.error.exception.ErrorCode;
import com.seoul.openproject.partner.error.exception.NoEntityException;
import com.seoul.openproject.partner.repository.match.MatchRepository;
import com.seoul.openproject.partner.repository.user.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
