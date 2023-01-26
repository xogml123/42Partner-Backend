package partner42.moduleapi.service.alarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SseEventName {
    ALARM_LIST("alarmList");

    private final String name;

}
