package partner42.moduleapi.service.random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import partner42.moduleapi.dto.match.MatchMakingDto;
import partner42.moduleapi.config.kafka.AlarmEvent;
import partner42.moduleapi.producer.random.MatchMakingEvent;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.sse.SseEventName;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.repository.random.RandomMatchRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatchMakingService {
    private static final String SYSTEM = "system";

    private final RandomMatchService randomMatchService;
    private final RandomMatchRepository randomMatchRepository;

    public MatchMakingDto matchMaking(MatchMakingEvent matchMakingEvent) {
        LocalDateTime now = matchMakingEvent.getNow();
        MatchMakingDto matchMakingDto = new MatchMakingDto();
        getMatchedGroupList(matchMakingEvent).forEach((matchedRandomMatches) -> {
            //3. 매칭이 완료된 데이터를 트랜잭션을 걸고 DB에 저장한다, 반영 중 취소등의 사유로 RandomMatch Entity에
            // 변경이 발생했을 경우 낙관적락 예외가 발생한다.
            try {
                Match match = randomMatchService.makeMatchInRDB(matchedRandomMatches, now);
                matchMakingDto.getEmails().add(match.getMatchMembers().stream()
                    .map(MatchMember::getMember)
                    .map(Member::getUser)
                    .map(User::getEmail)
                    .collect(Collectors.toList()));
                matchMakingDto.getAlarmEvents().add(match.getMatchMembers().stream()
                    .map(MatchMember::getMember)
                    .map(member -> new AlarmEvent(AlarmType.MATCH_CONFIRMED,
                        AlarmArgs.builder()
                            .opinionId(null)
                            .articleId(null)
                            .callingMemberNickname(SYSTEM)
                            .build(), member.getId(), SseEventName.ALARM_LIST))
                    .collect(Collectors.toList()));
            } catch (RuntimeException e) {
                log.info("{}: 매칭 과정 중 취소가 발생했습니다.",e.getClass());
            }
        });
        return matchMakingDto;
    }

    /**
     * 정렬된 매칭 리스트를 매칭 조건에 따라 그룹화한다.
     *
     * @return
     */
    private List<List<RandomMatch>> getMatchedGroupList(
        MatchMakingEvent matchMakingEvent) {
        //1. 랜덤 매칭 테이블에서 매칭 대기중인 데이터를 가져온다.
        List<RandomMatch> validAndSortedByRandomMatchConditionRandomMatches = randomMatchService.getValidRandomMatchesSortedByMatchCondition(
            matchMakingEvent);

        List<List<RandomMatch>> matchedRandomMatchesList = new ArrayList<>();
        List<RandomMatch> matchedRandomMatches = new ArrayList<>();
        //2-2. 매칭 조건에 따라 매칭을 진행한다.

        validAndSortedByRandomMatchConditionRandomMatches.forEach((randomMatch) -> {
            if (isMatchNotExpected(matchedRandomMatches, randomMatch)) {
                return;
            }
            //매칭 조건이 같은 요소가 이미 있는 경우
            matchedRandomMatches.add(randomMatch);
            //매칭인원이 모두 모인 경우
            if (matchedRandomMatches.size() == RandomMatch.MATCH_COUNT) {
                matchedRandomMatchesList.add(new ArrayList<>(matchedRandomMatches));

                Set<Long> matchedMemberIdSet = matchedRandomMatches.stream()
                    .map(RandomMatch::getMember)
                    .map(Member::getId)
                    .collect(Collectors.toSet());
                expireMatchedOnlyInMemory(validAndSortedByRandomMatchConditionRandomMatches,
                    matchedRandomMatches,
                    matchedMemberIdSet);

                matchedRandomMatches.clear();
            }
        });
        return matchedRandomMatchesList;
    }

    private boolean isMatchNotExpected(List<RandomMatch> matchedRandomMatches,
        RandomMatch randomMatch) {
        //만료된 매칭은 제외
        if (randomMatch.getIsExpired()) {
            return true;
        }
        //아직 같은 조건의 매칭이 하나도 없는 경우 하나 넣고 다음 반복으로넘어감
        if (matchedRandomMatches.isEmpty()) {
            matchedRandomMatches.add(randomMatch);
            return true;
        }
        //매칭 조건 다른 요소가 randomMatch인 경우 더 이상 같은 조건이 경우가 없는 경우이므로
        //matchedRandomMatches를 초기화하고 randomMatch를 넣고 다음 반복으로 넘어감
        if (!randomMatch.isMatchConditionEquals(matchedRandomMatches.get(0))) {
            matchedRandomMatches.clear();
            matchedRandomMatches.add(randomMatch);
            return true;
        }
        return false;
    }

    /**
     * 매칭 된 유저의 다른 모든 신청 조건 무효화 memberIdSet에 포함되고, 같은 contentCategory인 경우
     * Collection에서만 무효화
     * 트랜잭션이 걸려있지 않아서 DB에서는 변경 없음
     *
     * @param validAndSortedByRandomMatchConditionRandomMatches
     * @param matchedRandomMatches
     * @param matchedMemberIdSet
     */
    private void expireMatchedOnlyInMemory(
        List<RandomMatch> validAndSortedByRandomMatchConditionRandomMatches,
        List<RandomMatch> matchedRandomMatches, Set<Long> matchedMemberIdSet) {
        validAndSortedByRandomMatchConditionRandomMatches.stream()
            .filter(mrm -> matchedMemberIdSet.contains(mrm.getMember().getId())
                && mrm.getRandomMatchCondition().getContentCategory().equals(
                matchedRandomMatches.get(0).getRandomMatchCondition()
                    .getContentCategory()))
            .forEach(RandomMatch::expire);
    }


}
