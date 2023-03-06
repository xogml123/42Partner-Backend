//package partner42.modulecommon.repository.match;
//
//import static org.assertj.core.api.Assertions.*;
//
//import java.util.List;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.SliceImpl;
//import partner42.modulecommon.domain.model.match.ContentCategory;
//import partner42.modulecommon.domain.model.match.Match;
//import partner42.modulecommon.domain.model.match.MatchMember;
//import partner42.modulecommon.domain.model.match.MatchStatus;
//import partner42.modulecommon.domain.model.match.MethodCategory;
//import partner42.modulecommon.domain.model.member.Member;
//import partner42.modulecommon.repository.article.ArticleRepository;
//import partner42.modulecommon.repository.articlemember.ArticleMemberRepository;
//import partner42.modulecommon.repository.member.MemberRepository;
//import partner42.modulecommon.repository.user.UserRepository;
//import partner42.modulecommon.utils.CreateTestDataUtils;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class MatchRepositoryCustomImplTest {
//
//    @Autowired
//    UserRepository userRepository;
//    @Autowired
//    CreateTestDataUtils createTestDataUtils;
//    @Autowired
//    ArticleRepository articleRepository;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    ArticleMemberRepository articleMemberRepository;
//
//    @Autowired
//    MatchRepository matchRepository;
//
//    @Autowired
//    MatchMemberRepository matchMemberRepository;
//
//
//    @PersistenceContext
//    private EntityManager entityManager;
//    @BeforeEach
//    void setUp() {
//
//        Member member1 = Member.of("member1");
//        Member member2 = Member.of("member2");
//
//        memberRepository.saveAll(List.of(member1, member2));
//
//        Match match1 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.RANDOM,
//            null, 3);
//        matchRepository.save(match1);
//        matchMemberRepository.saveAll(List.of(MatchMember.of(match1, null, true),
//            MatchMember.of(match1, null, false)));
//
//
//        Match match2 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.RANDOM,
//            null, 3);
//        matchRepository.save(match2);
//        matchMemberRepository.saveAll(List.of(MatchMember.of(match2, null, false),
//            MatchMember.of(match2, null, true)));
//    }
//
//    @Test
//    void findAllMatchMemberId_givenDifferentMatchPropertyInMatchSearchAndByMemeberId_whenFind_then() {
//        //given
//        String article1Ttitle = "article1";
//        String article2Ttitle = "article2";
//
//
//        //then
//        MatchSearch matchSearch = new MatchSearch();
//        matchSearch.setContentCategory(ContentCategory.MEAL);
//
//        SliceImpl<Match> matchSlicesMeal = (SliceImpl<Match>) matchRepository.findAllMatchByMemberIdAndByMatchSearch(takim.getId(),
//            matchSearch, PageRequest.of(0, 1));
//
//        SliceImpl<Match> matchSlicesMealOver = (SliceImpl<Match>) matchRepository.findAllMatchByMemberIdAndByMatchSearch(takim.getId(),
//            matchSearch, PageRequest.of(0, 3));
//
//        matchSearch.setContentCategory(ContentCategory.STUDY);
//        SliceImpl<Match> matchSliceStudy = (SliceImpl<Match>) matchRepository.findAllMatchByMemberIdAndByMatchSearch(takim.getId(),
//            matchSearch, PageRequest.of(0, 3));
//        //
//        assertThat(
//            matchSlicesMeal.getContent().get(0).getArticle().getTitle().equals(article1Ttitle));
//        assertThat(matchSlicesMeal.hasNext()).isTrue();
//        assertThat(matchSlicesMealOver.hasNext()).isFalse();
//        assertThat(matchSliceStudy.getContent()).isEmpty();
//
//    }
//}