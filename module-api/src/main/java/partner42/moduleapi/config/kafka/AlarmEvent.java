package partner42.moduleapi.config.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.sse.SseEventName;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AlarmEvent {
    private AlarmType type;
    private AlarmArgs args;
    private Long userId;
    private SseEventName eventName;
}
