package partner42.modulecommon.repository.random;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import partner42.modulecommon.domain.model.match.ContentCategory;

@Getter
@Setter
@Builder
public class RandomMatchSearch {
    private LocalDateTime createdAt;
    private Long memberId;
    private Boolean isExpired;
    private ContentCategory contentCategory;
}
