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
import partner42.modulecommon.domain.model.random.MealRandomMatch;
import partner42.modulecommon.domain.model.random.MealRandomMatch.MatchConditionComparator;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.random.StudyRandomMatch;
import partner42.modulecommon.repository.match.MatchMemberRepository;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionMatchRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionRepository;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.random.RandomMatchRepository;
import partner42.modulecommon.utils.CustomTimeUtils;
import partner42.modulecommon.utils.slack.SlackBotApi;
import partner42.modulecommon.utils.slack.SlackBotService;

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
     * 테스트 케이스
     * 1. 여러 조건이 들어오는 경우 매칭이 하나의 조건으로 채결되면 다른 신청 무효화 - 안됨
     * 2. Meal 만 매칭이 가능.
     * 3. 만료시간 제대로 되는지 확인.
     * 4. 매칭 조건 다른 것들 섞여서 생성해도 잘 되는지 체크
     * 5. 같은 조건 먼저 신청한사람이 먼저 매칭되도록
     * @return
     */
    @Transactional
    public List<List<String>> matchMaking() {
        //배치 시작 시간 기준
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();

        //1. 랜덤 매칭 테이블에서 매칭 대기중인 데이터를 가져온다.
        List<MealRandomMatch> mealRandomMatches = randomMatchRepository.findMealByCreatedAtBeforeAndIsExpired(
            now.minusMinutes(
                RandomMatch.MAX_WAITING_TIME), false);

        List<StudyRandomMatch> studyRandomMatches = randomMatchRepository.findStudyByCreatedAtBeforeAndIsExpired(
            now.minusMinutes(
                RandomMatch.MAX_WAITING_TIME), false);
        //2. 랜덤 매칭 테이블에서 매칭을 조건에 따라 분류한다.
        //2-1. 매칭 조건에 따라 정렬한다.
        //매칭 조건 그 다음 생성 순서를 기준으로
        mealRandomMatches.sort(new MatchConditionComparator());
        studyRandomMatches.sort(new StudyRandomMatch.MatchConditionComparator());
        log.info("mealRandomMatches : {}", mealRandomMatches.toString());
        List<List<String>> matchedMembersEmailList = new ArrayList<>();
        //2-2. 매칭 조건에 따라 매칭을 진행한다.
        List<RandomMatch> matchedRandomMatches = new ArrayList<>();
        for (int i = 0; i < mealRandomMatches.size(); i++) {
            MealRandomMatch mealRandomMatch = mealRandomMatches.get(i);
            //만료된 매치은 제외
            if (mealRandomMatch.getIsExpired()) {
                continue;
            }
            if (matchedRandomMatches.isEmpty()) {
                matchedRandomMatches.add(mealRandomMatch);
            } else if ( matchedRandomMatches.size() > 0) {
                RandomMatch randomMatchForCompare = matchedRandomMatches.get(0);
                if (!mealRandomMatch.isMatchConditionEquals(randomMatchForCompare)) {
                    matchedRandomMatches.clear();
                    matchedRandomMatches.add(mealRandomMatch);
                } else {
                    matchedRandomMatches.add(mealRandomMatch);
                    //매칭 조건 맞춰지면 매칭 진행
                    if (matchedRandomMatches.size() == RandomMatch.MATCH_COUNT) {
                        Match match = makeMatchInRDB(matchedRandomMatches, now);

                        //RandomMatch 테이블 isExpired = true로 변경
                        //매칭 된 유저의 모든 신청 조건 무효화
                        matchedRandomMatches.stream()
                            .map(RandomMatch::getMember)
                            .forEach(member -> {
                                mealRandomMatches.stream()
                                    .filter(mrm -> mrm.getMember().equals(member))
                                    .forEach(RandomMatch::expire);
                            });
                        //
                        matchedRandomMatches.clear();
                        //매칭 맺어진 멤버의 email추가.
                        matchedMembersEmailList.add(getMatchedParticipantsEmails(
                            match));
                    }
                }
            }
        }

        for (int i = 0; i < studyRandomMatches.size(); i++) {
            StudyRandomMatch studyRandomMatch = studyRandomMatches.get(i);
            //만료된 매치은 제외
            if (studyRandomMatch.getIsExpired()) {
                continue;
            }
            if (matchedRandomMatches.isEmpty()) {
                matchedRandomMatches.add(studyRandomMatch);
            } else if ( matchedRandomMatches.size() > 0) {
                RandomMatch randomMatchForCompare = matchedRandomMatches.get(0);
                if (!studyRandomMatch.isMatchConditionEquals(randomMatchForCompare)) {
                    matchedRandomMatches.clear();
                    matchedRandomMatches.add(studyRandomMatch);

                } else {
                    matchedRandomMatches.add(studyRandomMatch);
                    //매칭 조건 맞춰지면 매칭 진행
                    if (matchedRandomMatches.size() == RandomMatch.MATCH_COUNT) {
                        Match match = makeMatchInRDB(matchedRandomMatches, now);

                        //RandomMatch 테이블 isExpired = true로 변경
                        //매칭 된 유저의 모든 신청 조건 무효화
                        matchedRandomMatches.stream()
                            .map(RandomMatch::getMember)
                            .forEach(member -> {
                                studyRandomMatches.stream()
                                    .filter(mrm -> mrm.getMember().equals(member))
                                    .forEach(RandomMatch::expire);
                            });
                        //
                        matchedRandomMatches.clear();
                        //매칭 맺어진 멤버의 email추가.
                        matchedMembersEmailList.add(getMatchedParticipantsEmails(
                            match));
                    }
                }
            }
        }
        return matchedMembersEmailList;
    }

    private void expireMatched(List<MealRandomMatch> mealRandomMatches,
        List<RandomMatch> matchedRandomMatches) {

    }

    private Match makeMatchInRDB(List<RandomMatch> matchedRandomMatches, LocalDateTime now) {
        //RDB에 Match에 저장, MatchMember저장
        RandomMatch randomMatch = matchedRandomMatches.get(0);
        Match match = Match.of(MatchStatus.MATCHED, randomMatch.getContentCategory(),
            MethodCategory.RANDOM, null, RandomMatch.MATCH_COUNT);
        matchRepository.save(match);
        for (RandomMatch matchedRandomMatch : matchedRandomMatches) {
            matchedRandomMatch.updateMatch(match);
        }
        //member
        List<Member> members = matchedRandomMatches.stream()
            .map(RandomMatch::getMember)
            .collect(Collectors.toList());
        createAndSaveMatchMembers(match, members);
        //matchCondition
        List<MatchCondition> matchConditions = createAndSaveMatchCondition(match);



        return match;
    }

    private void createAndSaveMatchMembers(Match match, List<Member> members) {
        members
            .forEach(member -> {
                matchMemberRepository.save(MatchMember.of(match, member, false));
            });
    }

    private List<String> getMatchedParticipantsEmails(Match match) {
        return match.getMatchMembers().stream()
            .map(matchMember -> matchMember.getMember().getUser().getEmail())
            .collect(Collectors.toList());
    }

    private List<MatchCondition> createAndSaveMatchCondition(Match match) {
        RandomMatch randomMatch = match.getRandomMatches().get(0);
        List<MatchCondition> matchConditions = new ArrayList<>();
        matchConditions.add(matchConditionRepository.findByValue(randomMatch.getPlace().toString())
            .orElseThrow(() ->
                new IllegalStateException(
                    "MatchCondition이 존재하지 않습니다. value : " + randomMatch.getPlace().toString())));

        if (randomMatch instanceof MealRandomMatch) {
            matchConditions.add(matchConditionRepository.findByValue(
                    ((MealRandomMatch) randomMatch).getWayOfEating().toString())
                .orElseThrow(() ->
                    new IllegalStateException("MatchCondition이 존재하지 않습니다. value : "
                        + ((MealRandomMatch) randomMatch).getWayOfEating().toString())));
        } else if (randomMatch instanceof StudyRandomMatch) {
            matchConditions.add(matchConditionRepository.findByValue(
                    ((StudyRandomMatch) randomMatch).getTypeOfStudy().toString())
                .orElseThrow(() ->
                    new IllegalStateException("MatchCondition이 존재하지 않습니다. value : "
                        + ((StudyRandomMatch) randomMatch).getTypeOfStudy().toString())));
        }

        matchConditionMatchRepository.saveAll(matchConditions.stream()
            .map((matchCondition) ->
                MatchConditionMatch.of(match, matchCondition)
            )
            .collect(Collectors.toList()));
        return matchConditions;

    }

}
