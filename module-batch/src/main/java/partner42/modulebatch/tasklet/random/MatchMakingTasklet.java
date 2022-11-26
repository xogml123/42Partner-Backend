package partner42.modulebatch.tasklet.random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import partner42.modulecommon.domain.model.article.ArticleMember;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.match.MatchStatus;
import partner42.modulecommon.domain.model.match.MethodCategory;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchConditionMatch;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.exception.SlackException;
import partner42.modulecommon.repository.match.MatchMemberRepository;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionMatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionRepository;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.random.RandomMatchRedisRepository;
import partner42.modulecommon.repository.random.RandomMatchRepository;
import partner42.modulecommon.utils.CustomTimeUtils;
import partner42.modulecommon.utils.RandomUtils;
import partner42.modulecommon.utils.redis.RedisTransactionUtil;
import partner42.modulecommon.utils.slack.SlackBotApi;
import partner42.modulecommon.utils.slack.SlackBotService;

@Slf4j
@RequiredArgsConstructor
@Component
//동시성 문제가 없고, jobParameter 사용이 불필요 하기 때문에 singletonScope가 더 적합하다고 생각.
//@StepScope
public class MatchMakingTasklet implements Tasklet {

    private final RandomMatchRedisRepository randomMatchRedisRepository;
    private final RandomMatchRepository randomMatchRepository;
    private final MatchMemberRepository matchMemberRepository;
    private final MemberRepository memberRepository;
    private final MatchRepository matchRepository;

    private final MatchConditionRepository matchConditionRepository;
    private final MatchConditionMatchRepository matchConditionMatchRepository;

    private final SlackBotApi slackBotApi;

    private final SlackBotService slackBotService;
    private final RedisTransactionUtil redisTransactionUtil;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {

        List<List<String>> matchedMembersEmailList = new ArrayList<>();
        Map<String, List<>>
        /**
         * redis 트랜잭션 시작,
         * redis에서 현재 매칭 내역을 조회할 때부터 트랜잭션을 시작하여
         * batch job 진행 도중에 사용자가 매칭을 취소할 수 없도록 한다.
         * 취소 요청이 먼저 처리 되었음에도 , 매칭이 진행되는 경우를 방지하기 위함.
         * redis 는 싱글스레드 기반이기 때문에 각 트랜잭션간에 Serializable 격리수준이 보장됨.
         */
        //배치 시작 시간 기준
        redisTransactionUtil.wrapTransaction(() -> {
            LocalDateTime now = CustomTimeUtils.nowWithoutNano();
            for (int categoryCount = 0; categoryCount < 2; ++categoryCount) {
                //각각 Meal, Study에 대해
                List<String> conditionList = categoryCount == 0 ? RandomMatch.MEAL_CONDITION_LIST
                    : RandomMatch.STUDY_CONDITION_LIST;
                ContentCategory contentCategory =
                    categoryCount == 0 ? ContentCategory.MEAL : ContentCategory.STUDY;
                //모든 방 랜덤한 순서로 가져오기
                //각 방에서 이미 매칭된 인원 목록 저장하여, 다음 방 매칭 맺기전에 제거 로직 수행.
                Set<String> accumulatedMatchedParticipants = new HashSet<>();
                RandomUtils.createRangeDistinctIntegerList(0, conditionList.size())
                    .forEach(i -> {
                        // 이전 방들에서 매칭이 이미끝난 인원을 제거.
                        String condition = conditionList.get(i);
                        randomMatchRedisRepository.deleteAllSortedSet(condition,
                            accumulatedMatchedParticipants.toArray());

                        //한 방의 모든 신청자에 대한 매칭 검사
                        List<String> participants = randomMatchRedisRepository.getAllSortedSet(
                            condition);
                        //먼저 신청한 인원부터 매칭을 시도. SortedSet이고 날짜에 따라 정렬되어있음.
                        int unmatchedIdx = 0;
                        String[] matchedParticipants = new String[RandomMatch.MATCH_COUNT];
                        //앞으로 매칭될 수 있는 인원이 있어야지 매칭 맺어줌.
                        while (RandomMatch.MATCH_COUNT <= participants.size() - unmatchedIdx) {
                            for (int index = 0; index < RandomMatch.MATCH_COUNT; index++) {
                                //매치 인원만큼 하나의 단위의 그룹에 포함시킴.
                                matchedParticipants[index] = participants.get(unmatchedIdx);
                                unmatchedIdx++;
                            }
                            accumulatedMatchedParticipants.addAll(List.of(matchedParticipants));
                            Match match = makeMatchInRDB(contentCategory, condition,
                                matchedParticipants, now);

                            //Redis현재 방에서 매칭 된 인원 제거
                            //제거중 실패하면 어떻게 할지

                            randomMatchRedisRepository.deleteAllSortedSet(condition,
                                matchedParticipants);
                        }
                    });

                //slack 알림 보내기. 비동기로 전환.
                List<String> matchedParticipantsEmails = getMatchedParticipantsEmails(
                    match);
                ArrayList<String> slackIds = new ArrayList<>();
                for (String email : matchedParticipantsEmails) {
                    Optional<String> slackId = slackBotApi.getSlackIdByEmail(
                        email);
                    if (slackId.isPresent()) {
                        slackIds.add(slackId.get());
                    }
                }
                log.info("slackIds : {}", slackIds.toString());
                try {
                    String MPIMId = slackBotApi.createMPIM(slackIds)
                        .orElseThrow(() -> new SlackException(ErrorCode.SLACK_ERROR));
                    slackBotApi.sendMessage(MPIMId, "매칭이 완료되었습니다. 대화방에서 매칭을 확인해주세요.\n"
                        + "만약, 초대 되지않은 유저가 있다면 slack에서 초대해주세요.\n"
                        + "slack에 등록된 email이 IntraId" + User.SEOUL_42
                        + " 형식으로 되어있지 않으면 초대 및 알림이 발송 되지 않을 수 있습니다.");
                } catch (Exception exception) {
                    log.error("slack error : {}", exception.getMessage());
                }
            }

        });
    }
        return RepeatStatus.FINISHED;
}

    @Transactional
    Match makeMatchInRDB(ContentCategory contentCategory, String condition,
        String[] matchedParticipants, LocalDateTime now) {
        //RDB에 Match에 저장, MatchMember저장
        Match match = Match.of(MatchStatus.MATCHED,
            contentCategory,
            MethodCategory.RANDOM,
            null,
            RandomMatch.MATCH_COUNT);
        matchRepository.save(match);

        List<Long> memberIds = parseMemberIds(matchedParticipants);
        //member
        List<Member> members = memberRepository.findAllById(memberIds);
        createAndSaveMatchMembers(match, members);

        //matchCondition
        List<MatchCondition> matchConditions = createAndSaveMatchCondition(condition, match);

        //RandomMatch 테이블 isExpired = true로 변경
        randomMatchRepository.bulkUpdateIsexpired(true,
            now.minusMinutes(RandomMatch.MAX_WAITING_TIME));

        return match;
    }

    private List<Long> parseMemberIds(String[] matchedParticipants) {
        List<Long> memberIds = new ArrayList<>();
        for (String participant : matchedParticipants) {
            memberIds.add(Long.getLong(
                RandomMatch.splitApplyingInfo(participant)[1]));
        }
        return memberIds;
    }

    private void createAndSaveMatchMembers(Match match, List<Member> members) {
        members
            .forEach(member -> {
                matchMemberRepository.save(MatchMember.of(match, member, false));
            });
    }

    private List<MatchCondition> createAndSaveMatchCondition(String condition, Match match) {
        List<MatchCondition> matchConditions = Arrays.stream(
                RandomMatch.splitKeyInfo(condition))
            //contentCategory는 제외
            .skip(1)
            .map((matchCondition) -> matchConditionRepository
                .findByValue(matchCondition)
                .orElseThrow(() -> new NoEntityException(
                    ErrorCode.ENTITY_NOT_FOUND)))
            .collect(Collectors.toList());

        matchConditionMatchRepository.saveAll(matchConditions.stream()
            .map((matchCondition) ->
                MatchConditionMatch.of(match, matchCondition)
            )
            .collect(Collectors.toList()));
        return matchConditions;

    }

    @Transactional(readOnly = true)
    List<String> getMatchedParticipantsEmails(Match match) {
        return match.getMatchMembers().stream()
            .map(matchMember -> matchMember.getMember().getUser().getEmail())
            .collect(Collectors.toList());
    }
}
