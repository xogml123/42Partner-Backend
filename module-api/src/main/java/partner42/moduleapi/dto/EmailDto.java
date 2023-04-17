package partner42.moduleapi.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import partner42.moduleapi.config.kafka.AlarmEvent;

@Getter
@Setter
@Builder
public class EmailDto<T> {

    private final T response;

    private final List<String> emails;

    private final List<AlarmEvent> alarmEventList;
}
