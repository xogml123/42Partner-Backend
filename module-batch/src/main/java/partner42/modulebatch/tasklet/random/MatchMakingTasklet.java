package partner42.modulebatch.tasklet.random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import partner42.modulebatch.service.random.MatchMakingTaskletService;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.match.MatchStatus;
import partner42.modulecommon.domain.model.match.MethodCategory;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchConditionMatch;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.MealRandomMatch;
import partner42.modulecommon.domain.model.random.MealRandomMatch.MatchConditionComparator;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.random.StudyRandomMatch;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.exception.SlackException;
import partner42.modulecommon.repository.match.MatchMemberRepository;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionMatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionRepository;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.random.RandomMatchRepository;
import partner42.modulecommon.utils.CustomTimeUtils;
import partner42.modulecommon.utils.RandomUtils;
import partner42.modulecommon.utils.slack.SlackBotApi;
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

        List<List<String>> matchedMembersEmailList = matchMakingTaskletService.matchMaking();

        /**
         *  slack 알림 보내기. 비동기.
         *  트랜잭션안에 포함시키면 예외 발생 가능하기때문에 분리.
         */
        for (List<String> emails : matchedMembersEmailList) {
            slackBotService.createSlackMIIM(emails);
        }
        return RepeatStatus.FINISHED;
    }
}
