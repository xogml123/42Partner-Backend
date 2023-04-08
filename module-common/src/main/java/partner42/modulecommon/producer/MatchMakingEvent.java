package partner42.modulecommon.producer;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import partner42.modulecommon.domain.model.random.RandomMatchCondition;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MatchMakingEvent {

    private LocalDateTime now;
    private RandomMatchCondition randomMatchCondition;
}
