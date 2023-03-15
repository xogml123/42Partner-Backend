package partner42.modulecommon.repository.random;

import java.util.List;
import java.util.Set;
import partner42.modulecommon.domain.model.random.RandomMatch;

public interface RandomMatchRepositoryCustom {


    List<RandomMatch> findForUpdateByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(RandomMatchSearch randomMatchSearch);
    List<RandomMatch> findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(RandomMatchSearch randomMatchSearch);

    void bulkUpdateOptimisticLockIsExpiredToTrueByIds(
        Set<RandomMatchBulkUpdateDto> randomMatchBulkUpdateDtos);
}
