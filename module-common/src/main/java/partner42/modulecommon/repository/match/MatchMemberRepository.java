package partner42.modulecommon.repository.match;

import partner42.modulecommon.domain.model.match.MatchMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchMemberRepository extends JpaRepository<MatchMember, Long> {

}