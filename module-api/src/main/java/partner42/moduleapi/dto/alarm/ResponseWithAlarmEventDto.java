package partner42.moduleapi.dto.alarm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import partner42.modulecommon.config.kafka.AlarmEvent;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseWithAlarmEventDto<T> {

    private T response;
    private AlarmEvent alarmEvent;
}
