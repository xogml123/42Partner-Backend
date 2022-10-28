package com.seoul.openproject.partner.domain.repository.matchcondition;

import com.seoul.openproject.partner.domain.model.match.MatchCondition;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchConditionRepository extends JpaRepository<MatchCondition, Long> {

    Optional<MatchCondition> findByValue(String value);
}

