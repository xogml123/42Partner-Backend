package partner42.modulebatch.tasklet.random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.repository.random.RandomMatchRedisRepository;
import partner42.modulecommon.utils.CustomTimeUtils;
import partner42.modulecommon.utils.RandomUtils;

@Slf4j
@RequiredArgsConstructor
@Component
//동시성 문제가 없고, jobParameter 사용이 불필요 하기 때문에 singletonScope가 더 적합하다고 생각합니다.
//@StepScope
public class ExpiringTasklet implements Tasklet {


    private final RandomMatchRedisRepository randomMatchRedisRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {
        //모든 방 목록 랜덤한 순서로 가져오기
        List<String> conditionList = RandomMatch.CONDITION_LIST;
        String[] randomConditionList = new String[conditionList.size()];
        //삭제가 이루어지는 시간 기준
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();
        //Redis에서 모든 방 조사해서 시간 지난 요청은 삭제
        RandomUtils.createRangeDistinctIntegerList(0, conditionList.size())
            .forEach(i -> {
                String condition = conditionList.get(i);
                //한방의 모든 신청자에 대한 신청 시간 적합성 검사
                List<String> deleteParticipants = new ArrayList<>();
                randomMatchRedisRepository.getAllSortedSet(condition)
                    .forEach(participant -> {

                        //{2021-01-03 00:00:00}/{id}
                        String[] timeAndId = participant.split(RandomMatch.ID_DELIMITER);
                        LocalDateTime appliedTime = LocalDateTime.parse(timeAndId[0]);
                        //만료된 신청인 경우
                        if (now.minusMinutes(RandomMatch.MAX_APPLY_TIME).isAfter(appliedTime)) {
                            deleteParticipants.add(participant);
                        }
                    });
                randomMatchRedisRepository.deleteAllSortedSet(condition, deleteParticipants.toArray());
            });
        return RepeatStatus.FINISHED;
    }

}
