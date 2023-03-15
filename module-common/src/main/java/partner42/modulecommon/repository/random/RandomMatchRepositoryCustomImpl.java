package partner42.modulecommon.repository.random;

import static partner42.modulecommon.domain.model.member.QMember.member;
import static partner42.modulecommon.domain.model.random.QRandomMatch.*;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.exception.ErrorCode;

@RequiredArgsConstructor
@Repository
public class RandomMatchRepositoryCustomImpl implements RandomMatchRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<RandomMatch> findForUpdateByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
        RandomMatchSearch randomMatchSearch) {
        return queryFactory.select(randomMatch)
            .from(randomMatch)
            .join(randomMatch.member, member)
            .where(isMemberId(randomMatchSearch.getMemberId()),
                isExpired(randomMatchSearch.getIsExpired()),
                isCreatedAtAfter(randomMatchSearch.getCreatedAt()),
                isContentCategory(randomMatchSearch.getContentCategory()))
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetch();
    }

    @Override
    public List<RandomMatch> findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
        RandomMatchSearch randomMatchSearch) {
        return queryFactory.select(randomMatch).distinct()
            .from(randomMatch)
            .join(randomMatch.member, member).fetchJoin()
            .where(isMemberId(randomMatchSearch.getMemberId()),
                isExpired(randomMatchSearch.getIsExpired()),
                isCreatedAtAfter(randomMatchSearch.getCreatedAt()),
                isContentCategory(randomMatchSearch.getContentCategory()))
            .fetch();
    }

    /**
     * 1. OptimisticLock을 통해 Lost Update가 발생하지 않도록함.
     * 2. 이것을 구현하기 위해 영속성 컨텍스트를 비워 준 후(최신 DB상태를 조회 하기 위해) Write_Lock과 함께 해당 Id의  Update이전 마지막 조회 시점의 version을 가져와서 Write_Lock을 건 후 트랜잭션 내에서 조회 했을 때version이 바뀌었거나 엔티티가 삭제되었는지 확인하고
     *   문제가 있는 경우 OptimisticLockException발생.
     * 3. 정상 Update가 가능한 경우 version을 1 증가 시키고 isExpired를 true로 변경.
     * 4. 벌크성 수정 쿼리는 영속성 컨텍스트를 무시하고 실행되므로, 영속성 컨텍스트를 초기화함.
     * @param randomMatchBulkUpdateDtos
     */
    @Override
    public void bulkUpdateOptimisticLockIsExpiredToTrueByIds(
        Set<RandomMatchBulkUpdateDto> randomMatchBulkUpdateDtos){
        // 영속성 컨텍스트를 초기화 하여 영속성 컨테스트가 아닌 현재 DB 상태를 조회해온다.
        em.flush();
        em.clear();
        List<Long> idList = randomMatchBulkUpdateDtos.stream().map(RandomMatchBulkUpdateDto::getId)
            .collect(
                Collectors.toList());
        List<RandomMatch> randomMatches = findWithPessimisticLockByIdsAndSortedById(
            idList);

        verifyVersion(randomMatches, randomMatchBulkUpdateDtos);

        queryFactory.update(randomMatch)
            .set(randomMatch.isExpired, true)
            .set(randomMatch.version, randomMatch.version.add(1))
            .where(isMemberIdIn(idList))
            .execute();

        em.flush();
        em.clear();
    }

    private void verifyVersion(List<RandomMatch> randomMatches,
        Set<RandomMatchBulkUpdateDto> randomMatchBulkUpdateDtos) {
        if (randomMatches.size() != randomMatchBulkUpdateDtos.size()) {
            throw new OptimisticLockException("Optimistic Lock Exception");
        }
        Map<Long, Long> idVersionMap = randomMatchBulkUpdateDtos.stream()
            .collect(Collectors.toMap(RandomMatchBulkUpdateDto::getId,
                RandomMatchBulkUpdateDto::getVersion));
        randomMatches.forEach(rm -> {
            if (!idVersionMap.get(rm.getId()).equals(rm.getVersion())) {
                throw new OptimisticLockException("Optimistic Lock Exception");
            }
        });

    }

    private List<RandomMatch> findWithPessimisticLockByIdsAndSortedById(List<Long> ids){
        return queryFactory.select(randomMatch)
            .from(randomMatch)
            .where(isMemberIdIn(ids))
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetch();
    }

    private BooleanExpression isMemberIdIn(Collection<Long> memberIds) {
        return memberIds == null ? null : member.id.in(memberIds);
    }


    private BooleanExpression isMemberId(Long memberId) {
        return memberId == null ? null : member.id.eq(memberId);
    }

    private BooleanExpression isExpired(Boolean isExpired) {
        return isExpired == null ? null: randomMatch.isExpired.eq(isExpired);
    }

    private BooleanExpression isCreatedAtAfter(LocalDateTime createdAt) {
        return createdAt == null ? null: randomMatch.createdAt.after(createdAt);
    }

    private BooleanExpression isContentCategory(ContentCategory contentCategory) {
        return contentCategory == null ? null : randomMatch.randomMatchCondition.contentCategory.eq(contentCategory);
    }
}
