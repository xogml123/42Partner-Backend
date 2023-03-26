package partner42.modulecommon.repository.alarm;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import partner42.modulecommon.domain.model.alarm.Alarm;

public interface AlarmRepositoryCustom {


    Slice<Alarm> findSliceByMemberId(Pageable pageable, Long memberId);

    void bulkUpdateAlarmIsReadToTrueInIdList(List<Long> idList);
}

