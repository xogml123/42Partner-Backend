package partner42.modulecommon.repository.matchcondition;

import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchConditionRepository extends JpaRepository<MatchCondition, Long> {

    Optional<MatchCondition> findByValue(String value);
}

