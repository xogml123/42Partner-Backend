package partner42.modulecommon.repository.match;

import org.springframework.data.domain.SliceImpl;
import partner42.modulecommon.domain.model.match.Match;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MatchRepositoryCustom {

    Slice<Match> findAllFetchJoinMatchMemberId(Long memberId, MatchSearch matchSearch, Pageable pageable);
}
