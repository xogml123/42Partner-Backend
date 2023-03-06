package partner42.modulecommon.repository.alarm;


import static partner42.modulecommon.domain.model.alarm.QAlarm.alarm;
import static partner42.modulecommon.domain.model.article.QArticle.article;
import static partner42.modulecommon.domain.model.member.QMember.member;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import partner42.modulecommon.domain.model.alarm.Alarm;
import partner42.modulecommon.domain.model.match.ContentCategory;

@Slf4j
@RequiredArgsConstructor
@Repository
public class AlarmRepositoryCustomImpl implements AlarmRepositoryCustom {


    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Alarm> findAlarmSliceByMemberId(Pageable pageable, Long memberId) {
        JPAQuery<Alarm> query = queryFactory.select(alarm)
            .from(alarm)
            .join(alarm.calledMember, member)
            .where(
                isMemberId(memberId)
            );
        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(alarm.getType(),
                alarm.getMetadata());
            query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
                pathBuilder.get(o.getProperty())));
        }
        List<Alarm> alarms = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();
        boolean hasnext = false;
        if (alarms.size() == pageable.getPageSize() + 1) {
            hasnext = true;
            alarms.remove(alarms.size() - 1);
        }
        return new SliceImpl<>(alarms, pageable, hasnext);
    }

    private BooleanExpression isMemberId(Long memberId) {
        return memberId == null? null: alarm.calledMember.id.eq(memberId);
    }

}
