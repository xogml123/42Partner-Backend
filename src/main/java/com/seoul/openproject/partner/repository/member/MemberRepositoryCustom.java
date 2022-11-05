package com.seoul.openproject.partner.repository.member;

import com.seoul.openproject.partner.domain.model.member.Member;

public interface MemberRepositoryCustom {

    Member findByUserId(Long userId);
}
