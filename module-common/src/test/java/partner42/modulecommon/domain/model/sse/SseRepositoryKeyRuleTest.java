package partner42.modulecommon.domain.model.sse;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SseRepositoryKeyRuleTest {

    @Test
    void toCompleteKeyWhichSpecifyOnlyOneValue() {
        SseRepositoryKeyRule sseRepositoryKeyRuleWithoutCreatedAt = new SseRepositoryKeyRule(1L,
            SseEventName.ALARM_LIST, null);
        LocalDateTime now = LocalDateTime.now();
        SseRepositoryKeyRule sseRepositoryKeyRuleWithCreatedAt = new SseRepositoryKeyRule(1L,
            SseEventName.ALARM_LIST, now);
        String sWithoutCreatedAt = sseRepositoryKeyRuleWithoutCreatedAt.toCompleteKeyWhichSpecifyOnlyOneValue();
        String sWithCreatedAt = sseRepositoryKeyRuleWithCreatedAt.toCompleteKeyWhichSpecifyOnlyOneValue();
        //then
        assertThat(sWithoutCreatedAt)
            .isEqualTo("1_alarmList_");
        assertThat(sWithCreatedAt)
            .isEqualTo("1_alarmList_" + now);
    }

    @Test
    void toKeyUserAndEventInfo() {
    }
}