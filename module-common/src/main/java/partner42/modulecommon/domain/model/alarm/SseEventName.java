package partner42.modulecommon.domain.model.alarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SseEventName {
    ALARM_LIST("alarmList");

    private final String value;

    public static SseEventName getEnumFromValue(String name) {
        for(SseEventName e: SseEventName.values()) {
            if (e.getValue().equals(name)) {
                return e;
            }
        }
        return null;// not found
    }

}
