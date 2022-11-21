package partner42.modulecommon.repository.match;

import java.util.Optional;
import partner42.modulecommon.domain.model.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long>, MatchRepositoryCustom {

    Optional<Match> findByApiId(String apiId);
}
