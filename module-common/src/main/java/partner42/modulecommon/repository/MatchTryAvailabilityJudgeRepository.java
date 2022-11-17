package partner42.modulecommon.repository;

import partner42.modulecommon.domain.model.tryjudge.MatchTryAvailabilityJudge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchTryAvailabilityJudgeRepository extends
    JpaRepository<MatchTryAvailabilityJudge, Long> {

}
