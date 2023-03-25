package partner42.moduleapi.service.article;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import partner42.moduleapi.TestBootstrapConfig;
import partner42.moduleapi.dto.article.ArticleDto;
import partner42.moduleapi.dto.article.ArticleOnlyIdResponse;
import partner42.moduleapi.dto.article.ArticleReadOneResponse;
import partner42.moduleapi.dto.article.ArticleReadResponse;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.dto.member.MemberDto;
import partner42.moduleapi.mapper.MatchConditionMapperImpl;
import partner42.moduleapi.mapper.MemberMapperImpl;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.article.ArticleMember;
import partner42.modulecommon.domain.model.match.ConditionCategory;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.ArticleMatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TimeOfEating;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.InvalidInputException;
import partner42.modulecommon.exception.NotAuthorException;
import partner42.modulecommon.producer.AlarmProducer;
import partner42.modulecommon.repository.article.ArticleRepository;
import partner42.modulecommon.repository.article.ArticleSearch;
import partner42.modulecommon.repository.articlemember.ArticleMemberRepository;
import partner42.modulecommon.repository.matchcondition.ArticleMatchConditionRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionRepository;
import partner42.modulecommon.repository.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ArticleService.class, MemberMapperImpl.class, MatchConditionMapperImpl.class,
    Auditor.class, QuerydslConfig.class,
    TestBootstrapConfig.class, BootstrapDataLoader.class})
class ArticleServiceWithDaoTest {

    @Autowired
    private UserRepository userRepository;
    @MockBean
    private AlarmProducer alarmProducer;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleMemberRepository articleMemberRepository;
    @Autowired
    private ArticleMatchConditionRepository articleMatchConditionRepository;
    @Autowired
    private MatchConditionRepository matchConditionRepository;
    @BeforeEach
    void setUp() {
    }

    @Test
    void createArticle() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        LocalDate date = LocalDate.now().plusDays(1);
        ArticleDto dto = ArticleDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .date(date)
            .participantNumMax(3)
            .content("content")
            .title("title")
            .anonymity(false)
            .matchConditionDto(
                MatchConditionDto.builder()
                    .placeList(List.of(Place.GAEPO, Place.SEOCHO))
                    .timeOfEatingList(List.of(TimeOfEating.DINNER, TimeOfEating.LUNCH))
                    .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                    .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE))
                    .build()
            ).build();

        //when
        ArticleOnlyIdResponse response = articleService.createArticle(takim.getUsername(), dto);
        Article article = articleRepository.findByApiIdAndIsDeletedIsFalse(response.getArticleId())
            .get();
        //then
        assertThat(article).extracting(Article::getDate, Article::getTitle, Article::getContent,
                Article::getAnonymity, Article::getIsComplete, Article::getParticipantNum, Article::getParticipantNumMax, Article::getContentCategory)
            .containsExactly(date, "title", "content", false, false, 1, 3, ContentCategory.MEAL);
        assertThat(article.getArticleMatchConditions()).extracting(
                ArticleMatchCondition::getMatchCondition)
            .extracting("value", "conditionCategory")
            .containsExactlyInAnyOrder(
                tuple(Place.GAEPO.name(), ConditionCategory.Place),
                tuple(Place.SEOCHO.name(), ConditionCategory.Place),
                tuple(TimeOfEating.DINNER.name(), ConditionCategory.TimeOfEating),
                tuple(TimeOfEating.LUNCH.name(), ConditionCategory.TimeOfEating),
                tuple(WayOfEating.DELIVERY.name(), ConditionCategory.WayOfEating),
                tuple(TypeOfStudy.INNER_CIRCLE.name(), ConditionCategory.TypeOfStudy)
            );
        assertThat(article.getArticleMembers())
            .extracting(ArticleMember::getMember, ArticleMember::getIsAuthor)
            .containsExactly(tuple(takim.getMember(), true));
    }

    @Test
    void deleteArticle() {
        User takim = userRepository.findByUsername("takim").get();
        LocalDate date = LocalDate.now().plusDays(1);

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, 3, ContentCategory.MEAL));
        ArticleMember am = articleMemberRepository.save(
            ArticleMember.of(takim.getMember(), true, article));
        ArticleMatchCondition amc = articleMatchConditionRepository.save(
            ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article));
        articleService.deleteArticle(takim.getUsername(), article.getApiId());
        Optional<Article> optionalArticle = articleRepository.findByApiId(article.getApiId());
        //then
        assertThat(optionalArticle).isNotPresent();
    }

    @Test
    void deleteArticle_whenNotAuthorUserDelete_thenThrow() {
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        LocalDate date = LocalDate.now().plusDays(1);

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, 3, ContentCategory.MEAL));
        ArticleMember am = articleMemberRepository.save(
            ArticleMember.of(takim.getMember(), true, article));
        ArticleMatchCondition amc = articleMatchConditionRepository.save(
            ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article));

        Optional<Article> optionalArticle = articleRepository.findByApiId(article.getApiId());
        //then
        assertThatThrownBy(() ->
            articleService.deleteArticle(sorkim.getUsername(), article.getApiId()))
            .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void softDelete() {
        User takim = userRepository.findByUsername("takim").get();
        LocalDate date = LocalDate.now().plusDays(1);

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, 3, ContentCategory.MEAL));
        ArticleMember am = articleMemberRepository.save(
            ArticleMember.of(takim.getMember(), true, article));
        ArticleMatchCondition amc = articleMatchConditionRepository.save(
            ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article));
        articleService.softDelete(takim.getUsername(), article.getApiId());
        //then
        articleRepository.findByApiId(article.getApiId()).ifPresent(
            (at) ->
                assertThat(at).extracting(Article::getIsDeleted)
                    .isEqualTo(true)
        );
    }

    @Test
    void softDelete_whenNotAuthorUserDelete_thenThrow() {
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        LocalDate date = LocalDate.now().plusDays(1);

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, 3, ContentCategory.MEAL));
        ArticleMember am = articleMemberRepository.save(
            ArticleMember.of(takim.getMember(), true, article));
        ArticleMatchCondition amc = articleMatchConditionRepository.save(
            ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name())
                .get(), article));
        //then
        assertThatThrownBy(() ->
            articleService.softDelete(sorkim.getUsername(), article.getApiId()))
            .isInstanceOf(InvalidInputException.class);

    }

    @Test
    void updateArticle() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        LocalDate date = LocalDate.now().plusDays(1);
        ArticleDto dto = ArticleDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .date(date)
            .participantNumMax(3)
            .content("content")
            .title("title")
            .anonymity(false)
            .matchConditionDto(
                MatchConditionDto.builder()
                    .placeList(List.of(Place.GAEPO, Place.SEOCHO))
                    .timeOfEatingList(List.of(TimeOfEating.DINNER, TimeOfEating.LUNCH))
                    .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                    .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE))
                    .build()
            ).build();
        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", true, 4, ContentCategory.STUDY));
        ArticleMember am = articleMemberRepository.save(
            ArticleMember.of(takim.getMember(), true, article));
        articleMatchConditionRepository.saveAll(
            List.of(
                ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article)
                )
            );

        ArticleOnlyIdResponse articleOnlyIdResponse = articleService.updateArticle(dto,
            takim.getUsername(), article.getApiId());
        Article updatedArticle = articleRepository.findByApiId(articleOnlyIdResponse.getArticleId()).get();

        //then
        assertThat(updatedArticle).extracting(Article::getDate, Article::getTitle, Article::getContent,
                Article::getAnonymity, Article::getIsComplete, Article::getParticipantNum, Article::getParticipantNumMax, Article::getContentCategory)
            .containsExactly(date, "title", "content", false, false, 1, 3, ContentCategory.MEAL);
        assertThat(article.getArticleMatchConditions()).extracting(
                ArticleMatchCondition::getMatchCondition)
            .extracting(MatchCondition::getValue,
                MatchCondition::getConditionCategory)
            .containsExactlyInAnyOrder(
                tuple(Place.GAEPO.name(), ConditionCategory.Place),
                tuple(Place.SEOCHO.name(), ConditionCategory.Place),
                tuple(TimeOfEating.DINNER.name(), ConditionCategory.TimeOfEating),
                tuple(TimeOfEating.LUNCH.name(), ConditionCategory.TimeOfEating),
                tuple(WayOfEating.DELIVERY.name(), ConditionCategory.WayOfEating),
                tuple(TypeOfStudy.INNER_CIRCLE.name(), ConditionCategory.TypeOfStudy)
            );
        assertThat(article.getArticleMembers())
            .extracting(ArticleMember::getMember, ArticleMember::getIsAuthor)
            .containsExactly(tuple(takim.getMember(), true));
    }

    @Test
    void updateArticle_whenNotAuthorUserUpdate_thenThrow() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        LocalDate date = LocalDate.now().plusDays(1);
        ArticleDto dto = ArticleDto.builder()
            .contentCategory(ContentCategory.MEAL)
            .date(date)
            .participantNumMax(3)
            .content("content")
            .title("title")
            .anonymity(false)
            .matchConditionDto(
                MatchConditionDto.builder()
                    .placeList(List.of(Place.GAEPO, Place.SEOCHO))
                    .timeOfEatingList(List.of(TimeOfEating.DINNER, TimeOfEating.LUNCH))
                    .wayOfEatingList(List.of(WayOfEating.DELIVERY))
                    .typeOfStudyList(List.of(TypeOfStudy.INNER_CIRCLE))
                    .build()
            ).build();
        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", true, 4, ContentCategory.STUDY));
        ArticleMember am = articleMemberRepository.save(
            ArticleMember.of(takim.getMember(), true, article));
        articleMatchConditionRepository.saveAll(
            List.of(
                ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article)
            )
        );
        //then
        assertThatThrownBy(() ->
            articleService.updateArticle(dto,
                sorkim.getUsername(), article.getApiId()))
            .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void readOneArticle() {
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        LocalDate date = LocalDate.now().plusDays(1);

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, 3, ContentCategory.MEAL));
        ArticleMember am = articleMemberRepository.save(
            ArticleMember.of(takim.getMember(), true, article));
        articleMatchConditionRepository.saveAll(
            List.of(
                ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article)
            )
        );
        ArticleReadOneResponse articleReadOneResponse = articleService.readOneArticle(
            takim.getUsername(), article.getApiId());
        //then
        assertThat(articleReadOneResponse)
            .extracting(ArticleReadOneResponse::getArticleId, ArticleReadOneResponse::getUserId,
                ArticleReadOneResponse::getTitle, ArticleReadOneResponse::getDate,
                ArticleReadOneResponse::getCreatedAt, ArticleReadOneResponse::getContent,
                ArticleReadOneResponse::getAnonymity, ArticleReadOneResponse::getIsToday,
                ArticleReadOneResponse::getParticipantNumMax, ArticleReadOneResponse::getParticipantNum,
                ArticleReadOneResponse::getContentCategory)
            .containsExactly(article.getApiId(), takim.getApiId(), "a", date,
                article.getCreatedAt(), "a", false, false, 3, 1,
                ContentCategory.MEAL);
        assertThat(articleReadOneResponse)
            .extracting(ArticleReadOneResponse::getMatchConditionDto)
            .extracting(MatchConditionDto::getPlaceList,
                MatchConditionDto::getTimeOfEatingList,
                MatchConditionDto::getWayOfEatingList,
                MatchConditionDto::getTypeOfStudyList
            )
            .usingRecursiveComparison().ignoringAllOverriddenEquals().ignoringCollectionOrder()
            .isEqualTo(List.of(List.of(Place.GAEPO),
                List.of(TimeOfEating.MIDNIGHT, TimeOfEating.BREAKFAST),
                List.of(WayOfEating.TAKEOUT),
                List.of()
            ));

        assertThat(articleReadOneResponse)
            .extracting(ArticleReadOneResponse::getParticipantsOrAuthor)
            .usingRecursiveComparison().ignoringAllOverriddenEquals().ignoringCollectionOrder()
            .isEqualTo(List.of(
                MemberDto.builder()
                    .nickname(takim.getMember().getNickname())
                    .isAuthor(true)
                    .isMe(true)
            ));

    }

    @Test
    void readOneArticle_whenReadByOtherUserOrNotAuthenticatedUser_thenIsMeFalse() {
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        LocalDate date = LocalDate.now().plusDays(1);

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, 3, ContentCategory.MEAL));
        ArticleMember am = articleMemberRepository.save(
            ArticleMember.of(takim.getMember(), true, article));
        articleMatchConditionRepository.saveAll(
            List.of(
                ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article)
            )
        );
        ArticleReadOneResponse articleReadOneResponseBySorkim = articleService.readOneArticle(
            sorkim.getUsername(), article.getApiId());
        ArticleReadOneResponse articleReadOneResponseByUnAuthenticated = articleService.readOneArticle(
            sorkim.getUsername(), article.getApiId());

        //then
        assertThat(articleReadOneResponseBySorkim)
            .extracting(ArticleReadOneResponse::getParticipantsOrAuthor)
            .usingRecursiveComparison().ignoringAllOverriddenEquals().ignoringCollectionOrder()
            .isEqualTo(List.of(
                MemberDto.builder()
                    .nickname(takim.getMember().getNickname())
                    .isAuthor(true)
                    .isMe(false)
            ));

        assertThat(articleReadOneResponseByUnAuthenticated)
            .extracting(ArticleReadOneResponse::getParticipantsOrAuthor)
            .usingRecursiveComparison().ignoringAllOverriddenEquals().ignoringCollectionOrder()
            .isEqualTo(List.of(
                MemberDto.builder()
                    .nickname(takim.getMember().getNickname())
                    .isAuthor(true)
                    .isMe(false)
            ));

    }

    @Test
    void readAllArticle() {
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        LocalDate date = LocalDate.now().plusDays(1);

        //when
        Article article1 = articleRepository.save(
            Article.of(date, "a", "a", false, 3, ContentCategory.MEAL));
        articleMemberRepository.save(
            ArticleMember.of(takim.getMember(), true, article1));
        articleMatchConditionRepository.saveAll(
            List.of(
                ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article1),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article1),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article1),
                ArticleMatchCondition.of(matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article1)
            )
        );

        Article article2 = articleRepository.save(
            Article.of(date, "a", "a", false, 3, ContentCategory.STUDY));
        articleMemberRepository.save(
            ArticleMember.of(sorkim.getMember(), true, article2));
        articleMatchConditionRepository.saveAll(
            List.of(
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article2),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(),
                    article2)
            )
        );
            List<ArticleReadResponse> articleReadResponses = articleService.readAllArticle(
            PageRequest.of(0, 10), new ArticleSearch()).getContent();
        //then
        assertThat(articleReadResponses)
            .extracting(ArticleReadResponse::getNickname, ArticleReadResponse::getUserId,
                ArticleReadResponse::getArticleId, ArticleReadResponse::getTitle,
                ArticleReadResponse::getContent, ArticleReadResponse::getDate,
                ArticleReadResponse::getCreatedAt, ArticleReadResponse::getAnonymity,
                ArticleReadResponse::getIsComplete, ArticleReadResponse::getIsToday,
                ArticleReadResponse::getParticipantNumMax,
                ArticleReadResponse::getParticipantNum, ArticleReadResponse::getContentCategory)
            .containsExactlyInAnyOrder(
                tuple("takim", takim.getApiId(), article1.getApiId(), "a", "a", date,
                    article1.getCreatedAt(), false, false, false, 3, 1, ContentCategory.MEAL),
                tuple("sorkim", sorkim.getApiId(), article2.getApiId(), "a", "a", date,
                    article2.getCreatedAt(), false, false, false, 3, 1, ContentCategory.STUDY)
            );
        assertThat(articleReadResponses)
            .extracting(ArticleReadResponse::getMatchConditionDto)
            .extracting(MatchConditionDto::getPlaceList, MatchConditionDto::getTimeOfEatingList,
                MatchConditionDto::getWayOfEatingList, MatchConditionDto::getTypeOfStudyList)
            .containsExactlyInAnyOrder(
                tuple(
                    List.of(Place.GAEPO),
                    List.of(TimeOfEating.MIDNIGHT, TimeOfEating.BREAKFAST),
                    List.of(WayOfEating.TAKEOUT),
                    List.of()
                ),
                tuple(
                    List.of(Place.GAEPO),
                    List.of(),
                    List.of(WayOfEating.TAKEOUT),
                    List.of()
                )
            );

    }

    @Test
    void participateArticle() {
    }

    @Test
    void participateCancelArticle() {
    }

    @Test
    void completeArticle() {
    }
}