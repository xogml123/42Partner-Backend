package partner42.modulecommon.repository.random;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RandomMatchBulkUpdateDto {

    private Long id;
    private Long version;
}
