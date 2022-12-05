package partner42.modulebatch.service.random;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import partner42.modulebatch.utils.CreateTestDataUtils;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.match.MatchSearch;
import partner42.modulecommon.repository.member.MemberRepository;


@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CreateTestDataUtils.class, MatchRepository.class, MemberRepository.class, MatchMakingTaskletService.class})
class MatchMakingTaskletServiceTest {

    @Autowired
    MatchMakingTaskletService matchMakingTaskletService;

    @Autowired
    CreateTestDataUtils createTestDataUtils;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void matchMaking() {
//        createTestDataUtils.signUpUsers();

        matchMakingTaskletService.matchMaking();
        Member takim = memberRepository.findByNickname("takim").get();
        //then
        MatchSearch matchSearch = new MatchSearch();
        assertThat(
            matchRepository.findAllFetchJoinMatchMemberId(takim.getId(), matchSearch, PageRequest.of(0, 3) ).getSize())
            .isEqualTo(1);



    }
}