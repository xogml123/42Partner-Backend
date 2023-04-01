package partner42.modulecommon.domain.model.sse;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SseEventNameTest {

    @Test
    void getEnumFromValue() {
        SseEventName alarmList = SseEventName.getEnumFromValue("alarmList");
        SseEventName notExist = SseEventName.getEnumFromValue("notExist");
        assertThat(alarmList).isEqualTo(SseEventName.ALARM_LIST);
        assertThat(notExist).isNull();
    }
}