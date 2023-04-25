package partner42.modulecommon.utils.slack;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@SpringBootTest(classes = {SlackBotApi.class})
class SlackBotApiTest {

    @Autowired
    private SlackBotApi slackBotApi;
    @Test
    void getSlackIdByEmail() {
        int iterations = 10; // Number of times to perform the method
        long totalTime = 0;

        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();

            // Call the method you want to test
            slackBotApi.getSlackIdByEmail("xogml951@gmail.com");

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            totalTime += duration;
        }

        long meanTime = totalTime / iterations;
        log.info("Mean time taken: {} ms", meanTime);
    }

    @Test
    void sendMessage() {
        int iterations = 10; // Number of times to perform the method
        long totalTime = 0;

        String slackId = slackBotApi.getSlackIdByEmail("xogml951@gmail.com").get();
        String slackId2 = slackBotApi.getSlackIdByEmail("xogml951@korea.ac.kr").get();
        String MPIMID = slackBotApi.createMPIM(List.of(slackId, slackId2)).get();
        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();

            // Call the method you want to test
            slackBotApi.sendMessage(MPIMID, "test");

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            totalTime += duration;
        }

        long meanTime = totalTime / iterations;
        log.info("sendMessage Mean time taken: {} ms", meanTime);
    }

    @Test
    void createMPIM() {
        int iterations = 10; // Number of times to perform the method
        long totalTime = 0;

        String slackId = slackBotApi.getSlackIdByEmail("xogml951@gmail.com").get();
        String slackId2 = slackBotApi.getSlackIdByEmail("xogml951@korea.ac.kr").get();

        for (int i = 0; i < iterations; i++) {
            long startTime = System.currentTimeMillis();

            // Call the method you want to test
            String MPIMID = slackBotApi.createMPIM(List.of(slackId, slackId2)).get();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            totalTime += duration;
        }

        long meanTime = totalTime / iterations;
        log.info("createMPIM Mean time taken: {} ms", meanTime);
    }
}