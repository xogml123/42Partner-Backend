package partner42.modulebatch.service.random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.match.MatchStatus;
import partner42.modulecommon.domain.model.match.MethodCategory;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchConditionMatch;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.repository.match.MatchMemberRepository;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionMatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionRepository;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.random.RandomMatchBulkUpdateDto;
import partner42.modulecommon.repository.random.RandomMatchRepository;
import partner42.modulecommon.repository.random.RandomMatchSearch;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatchMakingTaskletService {


    private final RandomMatchRepository randomMatchRepository;
    private final MatchMemberRepository matchMemberRepository;
    private final MemberRepository memberRepository;
    private final MatchRepository matchRepository;

    private final MatchConditionRepository matchConditionRepository;
    private final MatchConditionMatchRepository matchConditionMatchRepository;


    /**
     * 테스트 케이스 1. 여러 조건이 들어오는 경우 매칭이 하나의 조건으로 채결되면 다른 신청 무효화 - 안됨 2. Meal 만 매칭이 가능. 3. 만료시간 제대로 되는지
     * 확인. 4. 매칭 조건 다른 것들 섞여서 생성해도 잘 되는지 체크 5. 같은 조건 먼저 신청한사람이 먼저 매칭되도록
     *
     * @return
     */


    @Transactional(readOnly = true)
    public List<RandomMatch> getValidRandomMatchesSortedByMatchCondition(LocalDateTime now) {
        List<RandomMatch> randomMatches = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .createdAt(now.minusMinutes(RandomMatch.MAX_WAITING_TIME))
                .isExpired(false)
                .build());
        randomMatches.sort(new RandomMatch.MatchConditionComparator());
        return randomMatches;
    }

    @Transactional
    public Match makeMatchInRDB(List<RandomMatch> matchedRandomMatches, LocalDateTime now) {
        //RDB에 Match에 저장, MatchMember저장
        Match match = createAndSaveMatch(matchedRandomMatches);
        //RandomMatch에 isExpired = true로 업데이트
        randomMatchRepository.bulkUpdateOptimisticLockIsExpiredToTrueByIds(
            matchedRandomMatches.stream()
                .map(randomMatch -> RandomMatchBulkUpdateDto.builder()
                    .id(randomMatch.getId())
                    .version(randomMatch.getVersion())
                    .build())
                .collect(Collectors.toSet()));
        //matchMember
        createAndSaveMatchMembers(match, matchedRandomMatches);
        //matchCondition
        createAndSaveMatchCondition(match, matchedRandomMatches);
        return match;
    }

    private Match createAndSaveMatch(List<RandomMatch> matchedRandomMatches) {
        Match match = Match.of(MatchStatus.MATCHED,
            matchedRandomMatches.get(0).getRandomMatchCondition().getContentCategory(),
            MethodCategory.RANDOM, null, RandomMatch.MATCH_COUNT);
        matchRepository.save(match);
        return match;
    }

    private void createAndSaveMatchMembers(Match match, List<RandomMatch> matchedRandomMatches) {
        matchedRandomMatches.stream()
            .map(RandomMatch::getMember)
            .forEach(member -> {
                matchMemberRepository.save(MatchMember.of(match, member, false));
            });
    }

    private List<MatchCondition> createAndSaveMatchCondition(Match match, List<RandomMatch> matchedRandomMatches) {
        RandomMatch randomMatch = matchedRandomMatches.get(0);
        List<MatchCondition> matchConditions = new ArrayList<>();
        String errorMessage = "MatchCondition이 존재하지 않습니다. value : ";
        matchConditions.add(matchConditionRepository.findByValue(randomMatch.getRandomMatchCondition().getPlace().toString())
            .orElseThrow(() ->
                new IllegalStateException(
                    errorMessage + randomMatch.getRandomMatchCondition().getPlace().toString())));
        matchConditions.add(matchConditionRepository.findByValue(
                (randomMatch).getRandomMatchCondition().getWayOfEating().toString())
            .orElseThrow(() ->
                new IllegalStateException(errorMessage
                    + (randomMatch).getRandomMatchCondition().getWayOfEating().toString())));
        matchConditions.add(matchConditionRepository.findByValue(
                (randomMatch).getRandomMatchCondition().getTypeOfStudy().toString())
            .orElseThrow(() ->
                new IllegalStateException(errorMessage
                    + (randomMatch).getRandomMatchCondition().getTypeOfStudy().toString())));
        matchConditionMatchRepository.saveAll(matchConditions.stream()
            .map((matchCondition) ->
                MatchConditionMatch.of(match, matchCondition)
            )
            .collect(Collectors.toList()));
        return matchConditions;

    }

}
