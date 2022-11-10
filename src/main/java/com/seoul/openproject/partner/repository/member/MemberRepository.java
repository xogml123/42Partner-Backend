package com.seoul.openproject.partner.repository.member;

import com.seoul.openproject.partner.domain.model.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByApiId(String apiId);

}

