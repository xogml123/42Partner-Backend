package partner42.moduleapi.service.opinion;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import partner42.moduleapi.TestBootstrapConfig;
import partner42.moduleapi.dto.ListResponse;
import partner42.moduleapi.dto.opinion.OpinionDto;
import partner42.moduleapi.dto.opinion.OpinionOnlyIdResponse;
import partner42.moduleapi.dto.opinion.OpinionResponse;
import partner42.moduleapi.dto.opinion.OpinionUpdateRequest;
import partner42.moduleapi.mapper.OpinionMapperImpl;
import partner42.moduleapi.service.alarm.AlarmService;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.kafka.AlarmEvent;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.opinion.Opinion;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.producer.AlarmProducer;
import partner42.modulecommon.repository.article.ArticleRepository;
import partner42.modulecommon.repository.opinion.OpinionRepository;
import partner42.modulecommon.repository.user.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({OpinionService.class, OpinionMapperImpl.class, Auditor.class, QuerydslConfig.class,
    TestBootstrapConfig.class, BootstrapDataLoader.class})
class OpinionServiceWithDAOTest {
    @Autowired
    private OpinionService opinionService;
    @MockBean
    private AlarmProducer alarmProducer;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OpinionRepository opinionRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createOpinion_whenExistOrNotParentIdResponese_thenCheckLevelParentOpinion() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        Article article = articleRepository.save(Article.of(LocalDate.now(), "a", "a", false, 3, ContentCategory.MEAL));
        OpinionDto noParentId = OpinionDto.builder()
            .articleId(article.getApiId())
            .content("content")
            .level(1)
            .parentId(null)
            .build();

        OpinionDto wrongParentId = OpinionDto.builder()
            .articleId(article.getApiId())
            .content("content")
            .level(2)
            .parentId("wrongParentId")
            .build();

        //when
        OpinionOnlyIdResponse noParentOpinionId = opinionService.createOpinion(noParentId,
            takim.getUsername());

        OpinionDto parentOpinionDto = OpinionDto.builder()
            .articleId(article.getApiId())
            .content("content")
            .level(100)
            .parentId(noParentOpinionId.getOpinionId())
            .build();
        OpinionOnlyIdResponse hasParentOpinionId = opinionService.createOpinion(parentOpinionDto, takim.getUsername());
        Opinion noParentOpinion = opinionRepository.findByApiId(noParentOpinionId.getOpinionId()).get();
        Opinion hasParentOpinion = opinionRepository.findByApiId(hasParentOpinionId.getOpinionId()).get();
        //then
        assertThat(noParentOpinion.getLevel()).isEqualTo(1);
        assertThat(hasParentOpinion.getLevel()).isEqualTo(2);
        assertThat(noParentOpinion.getParentOpinion()).isNull();
        assertThat(hasParentOpinion.getParentOpinion()).isEqualTo(noParentOpinion);

        assertThatThrownBy(() -> opinionService.createOpinion(wrongParentId, takim.getUsername()))
            .isInstanceOf(NoEntityException.class);
    }

    @Test
    void updateOpinion() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        Article article = articleRepository.save(Article.of(LocalDate.now(), "a", "a", false, 3, ContentCategory.MEAL));

        String newContent = "newContent";

        //when
        Opinion opinion = opinionRepository.save(
            Opinion.of("content", takim.getMember(), article, null));

        opinionService.updateOpinion(OpinionUpdateRequest.builder()
            .content(newContent).build(), opinion.getApiId(), takim.getUsername());

        //then
        opinionRepository.findByApiId(opinion.getApiId()).ifPresent(op -> {
            assertThat(op.getContent()).isEqualTo(newContent);
        });
    }

    @Test
    void findAllOpinionsInArticle() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        Article article = articleRepository.save(Article.of(LocalDate.now(), "a", "a", false, 3, ContentCategory.MEAL));
        Article articleNotFound = articleRepository.save(Article.of(LocalDate.now(), "a", "a", false, 3, ContentCategory.MEAL));

        opinionRepository.save(Opinion.of("content1", takim.getMember(), article, null));
        opinionRepository.save(Opinion.of("content2", takim.getMember(), article, null));

        opinionRepository.save(Opinion.of("content3", takim.getMember(), articleNotFound, null));
        //when
        ListResponse<OpinionResponse> allOpinionsInArticle = opinionService.findAllOpinionsInArticle(
            article.getApiId());

        //then
        assertThat(allOpinionsInArticle.getValues()).extracting(OpinionResponse::getContent)
            .containsExactlyInAnyOrder("content1", "content2");
    }

    @Test
    void recoverableDeleteOpinion() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        Article article = articleRepository.save(Article.of(LocalDate.now(), "a", "a", false, 3, ContentCategory.MEAL));

        Opinion opinion = opinionRepository.save(
            Opinion.of("content1", takim.getMember(), article, null));

        //when
        OpinionOnlyIdResponse opinionOnlyIdResponse = opinionService.recoverableDeleteOpinion(
            opinion.getApiId(), takim.getUsername());
        //then
        assertThat(opinionOnlyIdResponse.getOpinionId()).isEqualTo(opinion.getApiId());
        opinionRepository.findByApiId(opinionOnlyIdResponse.getOpinionId()).ifPresent(op -> {
            assertThat(op.getIsDeleted()).isTrue();
        });
    }

    @Test
    void completeDeleteOpinion() {

        //given
        User takim = userRepository.findByUsername("takim").get();
        Article article = articleRepository.save(Article.of(LocalDate.now(), "a", "a", false, 3, ContentCategory.MEAL));

        Opinion opinion = opinionRepository.save(
            Opinion.of("content1", takim.getMember(), article, null));

        //when
        OpinionOnlyIdResponse opinionOnlyIdResponse = opinionService.completeDeleteOpinion(
            opinion.getApiId(), takim.getUsername());
        //then
        assertThat(opinionOnlyIdResponse.getOpinionId()).isEqualTo(opinion.getApiId());
        assertThat(
            opinionRepository.findByApiId(opinionOnlyIdResponse.getOpinionId())).isNotPresent();
    }

    @Test
    void getOneOpinion() {
        //given
        User takim = userRepository.findByUsername("takim").get();
        Article article = articleRepository.save(Article.of(LocalDate.now(), "a", "a", false, 3, ContentCategory.MEAL));

        Opinion opinion = opinionRepository.save(
            Opinion.of("content1", takim.getMember(), article, null));

        //when
        OpinionResponse oneOpinion = opinionService.getOneOpinion(
            opinion.getApiId());
        //then
        assertThat(oneOpinion).extracting(OpinionResponse::getOpinionId)
            .isEqualTo(opinion.getApiId());
    }
}