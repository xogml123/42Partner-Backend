package partner42.modulecommon.repository.articlemember;

import java.util.List;
import partner42.modulecommon.domain.model.article.ArticleMember;

public interface ArticleMemberRepositoryCustom {

    List<ArticleMember> findByArticleApiId(String articleApiId);
}
