package partner42.modulecommon.repository.articlemember;

import java.util.List;
import partner42.modulecommon.domain.model.article.ArticleMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleMemberRepository extends JpaRepository<ArticleMember, Long>, ArticleMemberRepositoryCustom {


}
