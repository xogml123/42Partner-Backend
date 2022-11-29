package partner42.modulebatch.config.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import partner42.modulebatch.job.random.RandomMatchJobConfig;


@Slf4j
@RequiredArgsConstructor
@Component
@EnableScheduling
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final RandomMatchJobConfig randomMatchJobConfig;

    /**
     * 스케쥴이 끝난지 5초후 다시 실행됨.
     */
    @Scheduled(fixedDelay = 5000L)
    public void randomMatchSchedule() {

        try{
            jobLauncher.run(randomMatchJobConfig.randomMatchJob(), new JobParameters());
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException |
            JobParametersInvalidException | JobRestartException e){
            log.error("{}", e.getMessage());
        }
    }


}
