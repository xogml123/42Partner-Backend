package partner42.modulecommon.repository.random;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.query.Param;
import partner42.modulecommon.domain.model.random.RandomMatch;

public interface RandomMatchRepositoryCustom {


    List<RandomMatch> findForUpdateByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(RandomMatchSearch randomMatchSearch);
    List<RandomMatch> findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(RandomMatchSearch randomMatchSearch);

}
