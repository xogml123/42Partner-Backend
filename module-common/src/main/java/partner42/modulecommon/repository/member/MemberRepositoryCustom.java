package partner42.modulecommon.repository.member;

import partner42.modulecommon.domain.model.member.Member;

public interface MemberRepositoryCustom {

    Member findByUserId(Long userId);
}
