package partner42.modulecommon.repository.matchcondition;

import partner42.modulecommon.domain.model.matchcondition.ArticleMatchCondition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleMatchConditionRepository extends
    JpaRepository<ArticleMatchCondition, Long> {

}
