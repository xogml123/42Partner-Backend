package partner42.modulecommon.repository.match;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.match.MatchStatus;
import partner42.modulecommon.domain.model.match.MethodCategory;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.repository.article.ArticleRepository;
import partner42.modulecommon.repository.articlemember.ArticleMemberRepository;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.config.BootstrapDataLoader;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MatchRepositoryCustomImplTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BootstrapDataLoader bootstrapDataLoader;
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ArticleMemberRepository articleMemberRepository;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    MatchMemberRepository matchMemberRepository;


    @PersistenceContext
    private EntityManager entityManager;
    @BeforeEach
    void setUp() {
    }

    @Test
    void findAllMatchMemberId_givenDifferentMatchPropertyInMatchSearchAndByMemeberId_whenFindByMatchSearchAndMemberId_thenContainsOnly() {
        //given
        Member member1 = Member.of("member1");
        Member member2 = Member.of("member2");

        memberRepository.saveAll(List.of(member1, member2));

        Match match1 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.RANDOM,
            null, 3);
        matchRepository.save(match1);
        matchMemberRepository.save(
            MatchMember.of(match1, member1, false));
        matchMemberRepository.save(
            MatchMember.of(match1, member2, false));


        Match match2 = Match.of(MatchStatus.MATCHED, ContentCategory.STUDY, MethodCategory.RANDOM,
            null, 3);
        matchRepository.save(match2);
        matchMemberRepository.save(
            MatchMember.of(match2, member1, true));



        //then

        MatchSearch matchSearchMeal = new MatchSearch();
        matchSearchMeal.setContentCategory(ContentCategory.MEAL);

        MatchSearch matchSearchRandom = new MatchSearch();
        matchSearchRandom.setMethodCategory(MethodCategory.RANDOM);
        MatchSearch matchSearch = new MatchSearch();

        PageRequest pageSize3 = PageRequest.of(0, 3);

        Slice<Match> matchSlicesMeal =matchRepository.findAllMatchByMemberIdAndByMatchSearch(member1.getId(),
            matchSearchMeal, pageSize3);

        Slice<Match> matchSlicesRandom =  matchRepository.findAllMatchByMemberIdAndByMatchSearch(member1.getId(),
            matchSearchRandom, pageSize3);

        Slice<Match> matchSliceMember2 = matchRepository.findAllMatchByMemberIdAndByMatchSearch(member2.getId(),
            matchSearch, pageSize3);
        //then
        assertThat(matchSlicesMeal.getContent()).containsOnly(match1);
        assertThat(matchSlicesRandom.getContent()).containsOnly(match1, match2);
        assertThat(matchSliceMember2.getContent()).containsOnly(match1);

    }

    @Test
    void findAllMatchMemberId_whenPageSizeNearEntireQuerySize_thenHasRightNextFlag() {
        //given
        Member member1 = Member.of("member1");
        Member member2 = Member.of("member2");

        memberRepository.saveAll(List.of(member1, member2));

        Match match1 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.RANDOM,
            null, 3);
        matchRepository.save(match1);
        matchMemberRepository.save(
            MatchMember.of(match1, member1, false));
        matchMemberRepository.save(
            MatchMember.of(match1, member2, false));


        Match match2 = Match.of(MatchStatus.MATCHED, ContentCategory.STUDY, MethodCategory.RANDOM,
            null, 3);
        matchRepository.save(match2);
        matchMemberRepository.save(
            MatchMember.of(match2, member1, true));

        //then
        MatchSearch matchSearch = new MatchSearch();

        PageRequest pageSizeSmaller = PageRequest.of(0, 1);

        PageRequest pageSizeEqual = PageRequest.of(0, 2);
        PageRequest pageSizeBigger = PageRequest.of(0, 3);
        PageRequest pageOffset = PageRequest.of(1, 1);

        Slice<Match> matchSlicePageSizeSmaller = matchRepository.findAllMatchByMemberIdAndByMatchSearch(member1.getId(),
            matchSearch, pageSizeSmaller);
        Slice<Match> matchSlicePageSizeEqual = matchRepository.findAllMatchByMemberIdAndByMatchSearch(member1.getId(),
            matchSearch, pageSizeEqual);
        Slice<Match> matchSlicePageSizeBigger = matchRepository.findAllMatchByMemberIdAndByMatchSearch(member1.getId(),
            matchSearch, pageSizeBigger);
        Slice<Match> matchSlicePageOffset = matchRepository.findAllMatchByMemberIdAndByMatchSearch(
            member1.getId(),
            matchSearch, pageOffset);

        //then
        assertThat(matchSlicePageSizeSmaller.hasNext()).isTrue();
        assertThat(matchSlicePageSizeEqual.hasNext()).isFalse();
        assertThat(matchSlicePageSizeBigger.hasNext()).isFalse();
        assertThat(matchSlicePageOffset.hasNext()).isFalse();
    }

    @Test
    void findAllMatchMemberId_whenSortByCreatedAt_thenContainsExactly() {
        //given
        Member member1 = Member.of("member1");
        Member member2 = Member.of("member2");

        memberRepository.saveAll(List.of(member1, member2));

        Match match1 = Match.of(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.RANDOM,
            null, 3);
        matchRepository.save(match1);
        matchMemberRepository.save(
            MatchMember.of(match1, member1, false));
        matchMemberRepository.save(
            MatchMember.of(match1, member2, false));

        Match match3 = Match.of(MatchStatus.MATCHED, ContentCategory.STUDY, MethodCategory.RANDOM,
            null, 3);
        matchRepository.save(match3);
        matchMemberRepository.save(
            MatchMember.of(match3, member1, true));

        Match match2 = Match.of(MatchStatus.MATCHED, ContentCategory.STUDY, MethodCategory.RANDOM,
            null, 3);
        matchRepository.save(match2);
        matchMemberRepository.save(
            MatchMember.of(match2, member1, true));

        //then
        MatchSearch matchSearch = new MatchSearch();
        PageRequest pageSortByCreatedAt = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "createdAt"));

        Slice<Match> matchSlicePageSortByCreatedAt = matchRepository.findAllMatchByMemberIdAndByMatchSearch(member1.getId(),
            matchSearch, pageSortByCreatedAt);

        //then
        assertThat(matchSlicePageSortByCreatedAt.getContent()).containsExactly(match1, match3, match2);

    }
}