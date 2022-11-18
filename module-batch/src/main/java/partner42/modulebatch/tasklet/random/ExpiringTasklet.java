package partner42.modulebatch.tasklet.random;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import partner42.modulecommon.repository.random.RandomMatchRedisRepository;

@RequiredArgsConstructor
@Component
@StepScope
public class ExpiringTasklet implements Tasklet {

    @Value("#{jobParameters[requestDate]}")
    private String requestDate;

    private final RandomMatchRedisRepository randomMatchRedisRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {
        //Redis에서 모든 방 조사해서 시간 지난 요청은 삭제
        randomMatchRedisRepository
        return null;
    }
}
