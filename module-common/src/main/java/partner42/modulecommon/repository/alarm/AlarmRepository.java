package partner42.modulecommon.repository.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import partner42.modulecommon.domain.model.alarm.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, AlarmRepositoryCustom {

}
