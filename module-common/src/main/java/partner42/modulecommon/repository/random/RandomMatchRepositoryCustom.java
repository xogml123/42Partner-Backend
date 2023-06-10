package partner42.modulecommon.repository.random;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.random.RandomMatchCondition;

public interface RandomMatchRepositoryCustom {


    List<RandomMatch> findForUpdateByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(RandomMatchSearch randomMatchSearch);
    List<RandomMatch> findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(RandomMatchSearch randomMatchSearch);

    List<RandomMatch> findByCreatedAtAfterAndIsExpiredAndRandomMatchConditionAndSortedByRandomMatchConditionAndCreatedAtASC(
        LocalDateTime createdAt,
        Boolean isExpired,
        RandomMatchConditionSearch randomMatchConditionSearch);
    void bulkUpdateOptimisticLockIsExpiredToTrueByIds(
        Set<RandomMatchBulkUpdateDto> randomMatchBulkUpdateDtos);
}
