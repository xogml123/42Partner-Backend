package com.seoul.openproject.partner.domain.repository.member;

import com.seoul.openproject.partner.domain.model.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}

