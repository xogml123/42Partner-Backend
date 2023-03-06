package partner42.modulecommon.repository.alarm;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import partner42.modulecommon.domain.model.alarm.Alarm;

public interface AlarmRepositoryCustom {


    Slice<Alarm> findAlarmSliceByMemberId(Pageable pageable, Long memberId);
}

