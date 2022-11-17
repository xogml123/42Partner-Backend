package partner42.modulecommon.repository.article;

import partner42.modulecommon.domain.model.article.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ArticleRepositoryCustom {

    Slice<Article> findSliceByCondition(Pageable pageable, ArticleSearch condition);
}
