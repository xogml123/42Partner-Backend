package partner42.modulecommon.repository.match;

import partner42.modulecommon.domain.model.match.ContentCategory;
import lombok.Getter;
import lombok.Setter;
import partner42.modulecommon.domain.model.match.MethodCategory;

@Getter
@Setter
public class MatchSearch {
    private ContentCategory contentCategory;

    private MethodCategory methodCategory;
}
