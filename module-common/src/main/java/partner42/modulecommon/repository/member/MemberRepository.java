package partner42.modulecommon.repository.member;

import java.util.List;
import partner42.modulecommon.domain.model.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByApiId(String apiId);

    Optional<Member> findByNickname(String nickname);
    List<Member> findAllByNicknameIn(List<String> nicknames);
}

