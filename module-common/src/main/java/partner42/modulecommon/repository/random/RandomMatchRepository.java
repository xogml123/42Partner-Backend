package partner42.modulecommon.repository.random;

import partner42.modulecommon.domain.model.random.RandomMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomMatchRepository extends JpaRepository<RandomMatch, Long>, RandomMatchRepositoryCustom {


}
