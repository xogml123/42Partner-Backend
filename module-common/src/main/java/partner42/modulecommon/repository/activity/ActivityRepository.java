package partner42.modulecommon.repository.activity;

import partner42.modulecommon.domain.model.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long>, ActivityRepositoryCustom {


}
