package partner42.modulecommon.repository.matchcondition;

import java.util.List;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchConditionRepository extends JpaRepository<MatchCondition, Long> {

    Optional<MatchCondition> findByValue(String value);

    List<MatchCondition> findByValueIn(List<String> values);
}

