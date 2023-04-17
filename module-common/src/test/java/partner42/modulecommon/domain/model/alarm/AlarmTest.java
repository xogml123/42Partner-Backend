package partner42.modulecommon.domain.model.alarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import partner42.modulecommon.domain.model.member.Member;

class AlarmTest {

    @Test
    void read() {
        Alarm alarm = Alarm.of(null, null, Member.of("takim"));
        alarm.read();
        assertThat(alarm.getIsRead()).isTrue();
    }
}