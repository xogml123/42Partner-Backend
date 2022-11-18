package partner42.modulecommon.repository.activity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import partner42.modulecommon.domain.model.match.ContentCategory;

@Getter
@Setter
public class ActivitySearch {

    @Value("${date.opening}")
    private LocalDateTime openingDate;

    private ContentCategory contentCategory;

    private LocalDateTime startTime = openingDate;

    private LocalDateTime endTime = LocalDateTime.now();
}
