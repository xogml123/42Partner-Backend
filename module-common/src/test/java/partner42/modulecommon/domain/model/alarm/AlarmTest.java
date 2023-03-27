package partner42.modulecommon.domain.model.alarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AlarmTest {

    @Test
    void read() {

        Alarm alarm = Alarm.of(null, null, null);
        alarm.read();
        assertThat(alarm.getIsRead()).isTrue();
    }
}