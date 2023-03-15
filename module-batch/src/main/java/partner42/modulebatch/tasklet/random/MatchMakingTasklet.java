package partner42.modulebatch.tasklet.random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import partner42.modulebatch.service.random.MatchMakingTaskletService;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.utils.CustomTimeUtils;
import partner42.modulecommon.utils.slack.SlackBotService;

@Slf4j
@RequiredArgsConstructor
@Component
//동시성 문제가 없고, jobParameter 사용이 불필요 하기 때문에 singletonScope가 더 적합하다고 생각.
//@StepScope
public class MatchMakingTasklet implements Tasklet {

    private final SlackBotService slackBotService;

    private final MatchMakingTaskletService matchMakingTaskletService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {
        //배치 시작 시간 기준
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();

        getMatchedGroupList(now).forEach((matchedRandomMatches) -> {
            //3. 매칭이 완료된 데이터를 트랜잭션을 걸고 DB에 저장한다, 반영 중 취소등의 변경이 발생했을 경우 예외가 발생한다..
            try {
                Match match = matchMakingTaskletService.makeMatchInRDB(matchedRandomMatches, now);
                slackBotService.createSlackMIIM(match.getMatchMembers().stream()
                    .map(MatchMember::getMember)
                    .map(Member::getUser)
                    .map(User::getEmail)
                    .collect(Collectors.toList()));
            } catch (RuntimeException e) {
                log.error("해당 매칭이 실패했습니다.");
                throw e;
            }
            /**
             *  slack 알림 보내기. 비동기.
             *  트랜잭션안에 포함시키면 안됨.
             */
        });
        return RepeatStatus.FINISHED;
    }


    /**
     * 정렬된 매칭 리스트를 매칭 조건에 따라 그룹화한다.
     *
     * @param now
     * @return
     */
    private List<List<RandomMatch>> getMatchedGroupList(
        LocalDateTime now) {
        //1. 랜덤 매칭 테이블에서 매칭 대기중인 데이터를 가져온다.

        List<RandomMatch> validAndSortedByRandomMatchConditionRandomMatches = matchMakingTaskletService.getValidRandomMatchesSortedByMatchCondition(
            now);

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
        if (!randomMatch.isMatchConditionEquals(matchedRandomMatches.get(0))) {
            matchedRandomMatches.clear();
            matchedRandomMatches.add(randomMatch);
            return true;
        }
        return false;
    }

    /**
     * 매칭 된 유저의 다른 모든 신청 조건 무효화 memberIdSet에 포함되고, 같은 contentCategory 트랜잭션이 걸려있지 않아서 DB에서는 변경 없음
     * Collection에서만 무효화
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
