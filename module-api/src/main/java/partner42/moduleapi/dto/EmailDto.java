package partner42.moduleapi.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailDto<T> {

    private final T response;

    private final List<String> emails;
}
