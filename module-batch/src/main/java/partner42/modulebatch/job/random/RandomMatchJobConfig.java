package partner42.modulebatch.job.random;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j // log 사용을 위한 lombok 어노테이션
@RequiredArgsConstructor // 생성자 DI를 위한 lombok 어노테이션
@Configuration
public class RandomMatchJobConfig {
    private final JobBuilderFactory jobBuilderFactory; // 생성자 DI 받음
    private final StepBuilderFactory stepBuilderFactory; // 생성자 DI 받음

    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")
            .start(simpleStep1())
            .build();
    }

//    @Bean
//    @JobScope
//    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
//        return stepBuilderFactory.get("simpleStep1")
//            .tasklet((contribution, chunkContext) -> {
//                log.info(">>>>> This is Step1");
//                log.info(">>>>> requestDate = {}", requestDate);
//                return RepeatStatus.FINISHED;
//            })
//            .build();
//    }

    @Bean
    public Step simpleStep1() {
        return stepBuilderFactory.get("simpleStep1")
            .tasklet((contribution, chunkContext) -> {
                log.info(">>>>> This is Step1");
                return RepeatStatus.FINISHED;
            })
            .build();
    }
}