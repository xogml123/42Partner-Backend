package com.seoul.openproject.partner.repository.match;

import com.seoul.openproject.partner.domain.model.match.MatchMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchMemberRepository extends JpaRepository<MatchMember, Long> {

}