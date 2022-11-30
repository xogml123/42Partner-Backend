package partner42.modulecommon.repository.article;

import partner42.modulecommon.domain.model.match.ContentCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleSearch {

    private Boolean isComplete;

    private ContentCategory contentCategory;

    private Boolean anonymity;
}
