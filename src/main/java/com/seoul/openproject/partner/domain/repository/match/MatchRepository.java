package com.seoul.openproject.partner.domain.repository.match;

import com.seoul.openproject.partner.domain.model.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {

}
