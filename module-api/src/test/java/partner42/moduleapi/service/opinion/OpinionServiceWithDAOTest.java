package partner42.moduleapi.service.opinion;


import static org.mockito.BDDMockito.*;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import partner42.moduleapi.TestBootstrapConfig;
import partner42.moduleapi.dto.opinion.OpinionDto;
import partner42.moduleapi.mapper.OpinionMapperImpl;
import partner42.moduleapi.service.alarm.AlarmService;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.kafka.AlarmEvent;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.producer.AlarmProducer;
import partner42.modulecommon.repository.article.ArticleRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({OpinionService.class, OpinionMapperImpl.class, Auditor.class, QuerydslConfig.class, TestBootstrapConfig.class, BootstrapDataLoader.class})
class OpinionServiceWithDAOTest {

    @Autowired
    private OpinionService opinionService;
    @MockBean
    private AlarmProducer alarmProducer;
    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createOpinion() {
        //given
        Article article = articleRepository.save(Article.of(null, null, null, null, null, null));
        OpinionDto.builder()
            .articleId(article.getApiId())
            .content("content")
            .level(1)
            .parentId(null)
            .build();
    }

    @Test
    void updateOpinion() {
    }

    @Test
    void findAllOpinionsInArticle() {
    }

    @Test
    void recoverableDeleteOpinion() {
    }

    @Test
    void completeDeleteOpinion() {
    }

    @Test
    void getOneOpinion() {
    }
}