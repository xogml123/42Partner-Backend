package com.seoul.openproject.partner.repository.match;

import com.seoul.openproject.partner.domain.model.match.Match;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MatchRepositoryCustom {

    Slice<Match> findAllFetchJoinMatchMemberId(Long memberId, MatchSearch matchSearch, Pageable pageable);
}
