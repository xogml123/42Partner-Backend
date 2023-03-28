package partner42.modulecommon.repository.random;


import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.random.RandomMatch;
import partner42.modulecommon.domain.model.random.RandomMatchCondition;
import partner42.modulecommon.repository.member.MemberRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QuerydslConfig.class, Auditor.class})
class RandomMatchRepositoryCustomImplTest {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RandomMatchRepository randomMatchRepository;

    @BeforeEach
    void setUp() {
    }
    @Test
    void findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory_givenDifferentRandomMatchRelevantRandomMatchSearch_whenDifferentRandomMatchSearch_thenContainsOnly() {
        //given
        Member member1 = memberRepository.save(Member.of("member1"));
        Member member2 = memberRepository.save(Member.of("member2"));

        RandomMatch randomMatchStudyMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, TypeOfStudy.INNER_CIRCLE), member1);

        RandomMatch randomMatchMealMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, WayOfEating.DELIVERY), member1);

        RandomMatch randomMatchMealExpiredMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.SEOCHO, WayOfEating.DELIVERY), member1);
        randomMatchMealExpiredMember1.expire();

        RandomMatch randomMatchStudyMember2 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, TypeOfStudy.INNER_CIRCLE), member2);

        randomMatchRepository.saveAll(
            List.of(randomMatchStudyMember1, randomMatchMealMember1, randomMatchMealExpiredMember1,
                randomMatchStudyMember2));
        //when
        List<RandomMatch> randomMatchesStudy = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .contentCategory(ContentCategory.STUDY)
                .build());

        List<RandomMatch> randomMatchesMember2 = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .memberId(member2.getId())
                .build());

        List<RandomMatch> randomMatchesCreatedAtNow = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .createdAt(LocalDateTime.now())
                .build());

        List<RandomMatch> randomMatchesCreatedAtBeforeThirtyMinute = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .build());

        List<RandomMatch> randomMatchesExpiredTrue = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .isExpired(true)
                .build());

        List<RandomMatch> randomMatchesMeal = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .contentCategory(ContentCategory.MEAL)
                .isExpired(false)
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .memberId(member1.getId())
                .build());

        //then

        assertThat(randomMatchesStudy).containsOnly(randomMatchStudyMember1, randomMatchStudyMember2);
        assertThat(randomMatchesMember2).containsOnly(randomMatchStudyMember2);
        assertThat(randomMatchesCreatedAtNow).isEmpty();
        assertThat(randomMatchesCreatedAtBeforeThirtyMinute).containsOnly(randomMatchStudyMember1, randomMatchMealMember1, randomMatchMealExpiredMember1, randomMatchStudyMember2);
        assertThat(randomMatchesExpiredTrue).containsOnly(randomMatchMealExpiredMember1);
    }

    @Test
    void findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory_whenFetchJoinNotWithDistinct_thenMemberEntityDoesNotReplicatedAndMemberFetchedEagerly() {
        //given
        Member member1 = memberRepository.save(Member.of("member1"));
        Member member2 = memberRepository.save(Member.of("member2"));

        RandomMatch randomMatchStudyMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, TypeOfStudy.INNER_CIRCLE), member1);

        RandomMatch randomMatchMealMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, WayOfEating.DELIVERY), member1);

        RandomMatch randomMatchMealExpiredMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.SEOCHO, WayOfEating.DELIVERY), member1);
        randomMatchMealExpiredMember1.expire();
        RandomMatch randomMatchStudyMember2 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, TypeOfStudy.INNER_CIRCLE), member2);

        randomMatchRepository.saveAll(
            List.of(randomMatchStudyMember1, randomMatchMealMember1, randomMatchMealExpiredMember1,
                randomMatchStudyMember2));
        //when
        List<RandomMatch> randomMatches = randomMatchRepository.findByCreatedAtAfterAndIsExpiredAndMemberIdAndContentCategory(
            RandomMatchSearch.builder()
                .build());
        //then
        assertThat(randomMatches).hasSize(4);
        assertThat(em.getEntityManagerFactory().getPersistenceUnitUtil()
            .isLoaded(randomMatches.get(0).getMember())).isTrue();
    }

    @Test
    void bulkUpdateOptimisticLockIsExpiredToTrueByIds_givenRandomMatches_whenBulkUpdate_thenUpdatedAndVersionChanged() {

        //given
        Member member1 = memberRepository.save(Member.of("member1"));
        Member member2 = memberRepository.save(Member.of("member2"));

        RandomMatch randomMatchStudyMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, TypeOfStudy.INNER_CIRCLE), member1);

        RandomMatch randomMatchMealMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, WayOfEating.DELIVERY), member1);

        RandomMatch randomMatchMealExpiredMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.SEOCHO, WayOfEating.DELIVERY), member1);
        randomMatchMealExpiredMember1.expire();
        RandomMatch randomMatchStudyMember2 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, TypeOfStudy.INNER_CIRCLE), member2);

        randomMatchRepository.saveAll(
            List.of(randomMatchStudyMember1, randomMatchMealMember1, randomMatchMealExpiredMember1,
                randomMatchStudyMember2));
        //when

        randomMatchRepository.bulkUpdateOptimisticLockIsExpiredToTrueByIds(
            Set.of(RandomMatchBulkUpdateDto.builder()
                    .id(randomMatchStudyMember1.getId())
                    .version(randomMatchStudyMember1.getVersion())
                    .build(),
                RandomMatchBulkUpdateDto.builder()
                    .id(randomMatchMealMember1.getId())
                    .version(randomMatchMealMember1.getVersion())
                    .build()));
        RandomMatch randomMatchStudy = randomMatchRepository.findById(randomMatchStudyMember1.getId())
            .get();
        RandomMatch randomMatchMeal = randomMatchRepository.findById(randomMatchMealMember1.getId())
            .get();

        //then
        assertThat(randomMatchStudy.getIsExpired()).isTrue();
        assertThat(randomMatchStudy.getVersion()).isEqualTo(randomMatchStudyMember1.getVersion() + 1);
        assertThat(randomMatchMeal.getIsExpired()).isTrue();
        assertThat(randomMatchMeal.getVersion()).isEqualTo(randomMatchMealMember1.getVersion() + 1);
    }

    @Test
    void bulkUpdateOptimisticLockIsExpiredToTrueByIds_whenUpdateWithVersionMismatch_thenException() {

        //given
        Member member1 = memberRepository.save(Member.of("member1"));
        Member member2 = memberRepository.save(Member.of("member2"));

        RandomMatch randomMatchStudyMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, TypeOfStudy.INNER_CIRCLE), member1);

        RandomMatch randomMatchMealMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, WayOfEating.DELIVERY), member1);

        RandomMatch randomMatchMealExpiredMember1 = RandomMatch.of(
            RandomMatchCondition.of(Place.SEOCHO, WayOfEating.DELIVERY), member1);
        randomMatchMealExpiredMember1.expire();
        RandomMatch randomMatchStudyMember2 = RandomMatch.of(
            RandomMatchCondition.of(Place.GAEPO, TypeOfStudy.INNER_CIRCLE), member2);

        randomMatchRepository.saveAll(
            List.of(randomMatchStudyMember1, randomMatchMealMember1, randomMatchMealExpiredMember1,
                randomMatchStudyMember2));
        //when
        // then

        assertThatThrownBy(() -> randomMatchRepository.bulkUpdateOptimisticLockIsExpiredToTrueByIds(
            Set.of(RandomMatchBulkUpdateDto.builder()
                    .id(randomMatchStudyMember1.getId())
                    .version(randomMatchStudyMember1.getVersion() + 1)
                    .build(),
                RandomMatchBulkUpdateDto.builder()
                    .id(randomMatchMealMember1.getId())
                    .version(randomMatchMealMember1.getVersion())
                    .build()))).hasRootCauseInstanceOf(OptimisticLockException.class);

    }

}