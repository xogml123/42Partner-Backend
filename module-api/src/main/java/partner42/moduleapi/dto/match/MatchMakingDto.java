package partner42.moduleapi.dto.match;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import partner42.moduleapi.config.kafka.AlarmEvent;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchMakingDto {

    private List<List<String>> emails = new ArrayList<>();
    private List<List<AlarmEvent>> alarmEvents = new ArrayList<>();
}
