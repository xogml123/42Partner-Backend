package com.seoul.openproject.partner.repository;

import com.seoul.openproject.partner.domain.model.tryjudge.MatchTryAvailabilityJudge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchTryAvailabilityJudgeRepository extends
    JpaRepository<MatchTryAvailabilityJudge, Long> {

}
