package partner42.moduleapi.service.article;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import partner42.moduleapi.config.ServiceWithDAOTestDefaultConfig;
import partner42.moduleapi.config.TestBootstrapConfig;
import partner42.moduleapi.config.JpaPackage.JpaAndEntityPackagePathConfig;
import partner42.moduleapi.dto.EmailDto;
import partner42.moduleapi.dto.alarm.ResponseWithAlarmEventDto;
import partner42.moduleapi.dto.article.ArticleDto;
import partner42.moduleapi.dto.article.ArticleOnlyIdResponse;
import partner42.moduleapi.dto.article.ArticleReadOneResponse;
import partner42.moduleapi.dto.article.ArticleReadResponse;
import partner42.moduleapi.dto.match.MatchOnlyIdResponse;
import partner42.moduleapi.dto.matchcondition.MatchConditionDto;
import partner42.moduleapi.dto.member.MemberDto;
import partner42.moduleapi.mapper.MemberMapperImpl;
import partner42.moduleapi.service.alarm.AlarmService;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.kafka.AlarmEvent;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.article.ArticleMember;
import partner42.modulecommon.domain.model.match.ConditionCategory;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.match.Match;
import partner42.modulecommon.domain.model.match.MatchMember;
import partner42.modulecommon.domain.model.match.MatchStatus;
import partner42.modulecommon.domain.model.match.MethodCategory;
import partner42.modulecommon.domain.model.matchcondition.ArticleMatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchConditionMatch;
import partner42.modulecommon.domain.model.matchcondition.Place;
import partner42.modulecommon.domain.model.matchcondition.TimeOfEating;
import partner42.modulecommon.domain.model.matchcondition.TypeOfStudy;
import partner42.modulecommon.domain.model.matchcondition.WayOfEating;
import partner42.modulecommon.domain.model.sse.SseEventName;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.BusinessException;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.InvalidInputException;
import partner42.modulecommon.producer.AlarmProducer;
import partner42.modulecommon.repository.article.ArticleRepository;
import partner42.modulecommon.repository.article.ArticleSearch;
import partner42.modulecommon.repository.articlemember.ArticleMemberRepository;
import partner42.modulecommon.repository.match.MatchRepository;
import partner42.modulecommon.repository.matchcondition.ArticleMatchConditionRepository;
import partner42.modulecommon.repository.matchcondition.MatchConditionRepository;
import partner42.modulecommon.repository.sse.SSEInMemoryRepository;
import partner42.modulecommon.repository.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ArticleService.class, MemberMapperImpl.class,
    ServiceWithDAOTestDefaultConfig.class,
})
class ArticleServiceWithDaoTest {

    @Autowired
    private UserRepository userRepository;
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
    @Autowired
    private MatchRepository matchRepository;

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
                Article::getAnonymity, Article::getIsComplete, Article::getParticipantNum,
                Article::getParticipantNumMax, Article::getContentCategory)
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
            ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(),
                article));
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
            ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(),
                article));

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
            ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(),
                article));
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
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(),
                    article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(),
                    article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article)
            )
        );

        ArticleOnlyIdResponse articleOnlyIdResponse = articleService.updateArticle(dto,
            takim.getUsername(), article.getApiId());
        Article updatedArticle = articleRepository.findByApiId(articleOnlyIdResponse.getArticleId())
            .get();

        //then
        assertThat(updatedArticle).extracting(Article::getDate, Article::getTitle,
                Article::getContent,
                Article::getAnonymity, Article::getIsComplete, Article::getParticipantNum,
                Article::getParticipantNumMax, Article::getContentCategory)
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
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(),
                    article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(),
                    article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article)
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
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(),
                    article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(),
                    article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article)
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
                ArticleReadOneResponse::getParticipantNumMax,
                ArticleReadOneResponse::getParticipantNum,
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
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(),
                    article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(),
                    article),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article)
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
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article1),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(),
                    article1),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(),
                    article1),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(),
                    article1)
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
    void readAllArticle_whenPageSizeNearFindAllSize_thenSliceHasNext() {
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
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article1),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(),
                    article1),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(),
                    article1),
                ArticleMatchCondition.of(
                    matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(),
                    article1)
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
        SliceImpl<ArticleReadResponse> articleReadResponsesUnder = articleService.readAllArticle(
            PageRequest.of(0, 1), new ArticleSearch());
        SliceImpl<ArticleReadResponse> articleReadResponsesEquals = articleService.readAllArticle(
            PageRequest.of(0, 2), new ArticleSearch());
        SliceImpl<ArticleReadResponse> articleReadResponsesOver = articleService.readAllArticle(
            PageRequest.of(0, 3), new ArticleSearch());
        //then
        assertThat(articleReadResponsesUnder.hasNext()).isTrue();
        assertThat(articleReadResponsesEquals.hasNext()).isFalse();
        assertThat(articleReadResponsesOver.hasNext()).isFalse();
    }

    @Test
    void participateArticle() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        LocalDate date = LocalDate.now().plusDays(1);
        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, 3, ContentCategory.MEAL));
        ArticleMember.of(takim.getMember(), true, article);

        ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article);

        ResponseWithAlarmEventDto<ArticleOnlyIdResponse> dto = articleService.participateArticle(
            sorkim.getUsername(), article.getApiId());
        Article participatedArticle = articleRepository.findByApiId(
                dto.getResponse().getArticleId()).get();
        //then

        assertThat(participatedArticle).extracting(Article::getParticipantNum)
            .isEqualTo(2);
        assertThat(participatedArticle.getArticleMembers())
            .extracting(ArticleMember::getIsAuthor, ArticleMember::getMember)
            .containsExactlyInAnyOrder(tuple(true, takim.getMember()),
                tuple(false, sorkim.getMember()));
    }

    @Test
    void participateArticle_whenArticleIsFullOrAlreadyParticipatedUser_thenThrow() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        LocalDate date = LocalDate.now().plusDays(1);
        int participantNumMax = 2;

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, participantNumMax, ContentCategory.MEAL));
        ArticleMember.of(takim.getMember(), true, article);
        ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article);

        ResponseWithAlarmEventDto<ArticleOnlyIdResponse> dto = articleService.participateArticle(
            sorkim.getUsername(), article.getApiId());
        //then
        assertThatThrownBy(() -> articleService.participateArticle(sorkim.getUsername(), article.getApiId()))
            .isInstanceOf(InvalidInputException.class);

        assertThatThrownBy(() -> articleService.participateArticle(hyenam.getUsername(), article.getApiId()))
            .isInstanceOf(BusinessException.class)
            .hasMessage(ErrorCode.FULL_ARTICLE.getMessage());
    }


    @Test
    void participateCancelArticle() {
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();

        LocalDate date = LocalDate.now().plusDays(1);
        int participantNumMax = 2;

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, participantNumMax, ContentCategory.MEAL));
        ArticleMember.of(takim.getMember(), true, article);
        ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article);

        ResponseWithAlarmEventDto<ArticleOnlyIdResponse> dtoParticipate = articleService.participateArticle(
            sorkim.getUsername(), article.getApiId());
        ResponseWithAlarmEventDto<ArticleOnlyIdResponse> dtoParticipateCancel = articleService.participateCancelArticle(
            sorkim.getUsername(), article.getApiId());
        Article participatedArticle = articleRepository.findByApiId(
            dtoParticipate.getResponse().getArticleId()).get();
        //then

        assertThat(participatedArticle).extracting(Article::getParticipantNum)
            .isEqualTo(1);

        assertThat(participatedArticle.getArticleMembers())
            .extracting(ArticleMember::getIsAuthor, ArticleMember::getMember)
            .containsExactlyInAnyOrder(tuple(true, takim.getMember()));

    }


    @Test
    void participateCancelArticle_whenNotParticipatedUserCancel_thenThrow() {
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        LocalDate date = LocalDate.now().plusDays(1);
        int participantNumMax = 2;

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, participantNumMax, ContentCategory.MEAL));
        ArticleMember.of(takim.getMember(), true, article);
        ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article);
        articleService.participateArticle(sorkim.getUsername(), article.getApiId());

        //then
        assertThatThrownBy(() ->
            articleService.participateCancelArticle(
                hyenam.getUsername(), article.getApiId()))
            .isInstanceOf(InvalidInputException.class)
            .hasMessage(ErrorCode.NOT_PARTICIPATED_MEMBER.getMessage());

    }

    @Test
    void participateCancelArticle_whenAuthorMember_thenThrow() {
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        LocalDate date = LocalDate.now().plusDays(1);
        int participantNumMax = 2;

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, participantNumMax, ContentCategory.MEAL));
        ArticleMember.of(takim.getMember(), true, article);
        ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article);

        //then
        assertThatThrownBy(() ->
            articleService.participateCancelArticle(
                takim.getUsername(), article.getApiId()))
            .isInstanceOf(InvalidInputException.class)
            .hasMessage(ErrorCode.NOT_ALLOW_AUTHOR_MEMBER_DELETE.getMessage());
    }

    @Test
    void completeArticle() {
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        LocalDate date = LocalDate.now().plusDays(1);
        int participantNumMax = 3;

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, participantNumMax, ContentCategory.MEAL));
        ArticleMember.of(takim.getMember(), true, article);
        ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article);

        articleService.participateArticle(sorkim.getUsername(), article.getApiId());

        EmailDto<MatchOnlyIdResponse> matchOnlyIdResponseEmailDto = articleService.completeArticle(
            takim.getUsername(), article.getApiId());
        Match match = matchRepository.findByApiId(
            matchOnlyIdResponseEmailDto.getResponse().getMatchId()).get();

        //then
        assertThat(match).extracting(Match::getMatchStatus, Match::getContentCategory,
            Match::getMethodCategory, Match::getParticipantNum)
            .containsExactly(MatchStatus.MATCHED, ContentCategory.MEAL, MethodCategory.MANUAL,
                2);
        assertThat(match.getMatchMembers())
            .extracting(MatchMember::getMember, MatchMember::getIsAuthor,
                MatchMember::getIsReviewed)
            .containsExactly(
                tuple(takim.getMember(), true, false),
                tuple(sorkim.getMember(), false, false)
            );
        assertThat(match.getMatchConditionMatches())
            .extracting(MatchConditionMatch::getMatchCondition)
            .extracting(MatchCondition::getValue)
            .containsExactlyInAnyOrder(
                Place.GAEPO.name(),
                TimeOfEating.MIDNIGHT.name(),
                TimeOfEating.BREAKFAST.name(),
                WayOfEating.TAKEOUT.name()
            );
        assertThat(matchOnlyIdResponseEmailDto.getEmails())
            .containsExactlyInAnyOrder(
                takim.getEmail(),
                sorkim.getEmail()
            );
    }

    @Test
    void completeArticle_whenCompleteTwice_thenThrow() {
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        LocalDate date = LocalDate.now().plusDays(1);
        int participantNumMax = 3;

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, participantNumMax, ContentCategory.MEAL));
        ArticleMember.of(takim.getMember(), true, article);
        ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(),
            article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article);

        EmailDto<MatchOnlyIdResponse> matchOnlyIdResponseEmailDto = articleService.completeArticle(
            takim.getUsername(), article.getApiId());
        assertThatThrownBy(() ->
            articleService.completeArticle(
                takim.getUsername(), article.getApiId()))
            .isInstanceOf(BusinessException.class)
            .hasMessage(ErrorCode.COMPLETED_ARTICLE.getMessage());
    }

    @Test
    void completeArticle_whenNotAuthorComplete_thenThrow() {
        User takim = userRepository.findByUsername("takim").get();
        User sorkim = userRepository.findByUsername("sorkim").get();
        User hyenam = userRepository.findByUsername("hyenam").get();

        LocalDate date = LocalDate.now().plusDays(1);
        int participantNumMax = 3;

        //when
        Article article = articleRepository.save(
            Article.of(date, "a", "a", false, participantNumMax, ContentCategory.MEAL));
        ArticleMember.of(takim.getMember(), true, article);
        ArticleMatchCondition.of(matchConditionRepository.findByValue(Place.GAEPO.name()).get(),
            article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.MIDNIGHT.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(TimeOfEating.BREAKFAST.name()).get(), article);
        ArticleMatchCondition.of(
            matchConditionRepository.findByValue(WayOfEating.TAKEOUT.name()).get(), article);

        articleService.participateArticle(sorkim.getUsername(), article.getApiId());
        //then
        assertThatThrownBy(() ->
            articleService.completeArticle(
                sorkim.getUsername(), article.getApiId()))
            .isInstanceOf(InvalidInputException.class)
            .hasMessage(ErrorCode.NOT_ARTICLE_AUTHOR.getMessage());
    }
}