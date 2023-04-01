package partner42.modulecommon.repository.article;


import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.config.BootstrapDataLoader;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QuerydslConfig.class, Auditor.class})
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private BootstrapDataLoader bootstrapDataLoader;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void findDistinctFetchArticleMatchConditionsByApiIdAndIsDeletedIsFalse_givenOneDeletedArticleAndNotDeletedArticle_whenFindByApiId_thenNotDeletedOneApiIdIsEqualToAndDeletedOneIsNotPresent() {
        //given
        Article article2 = articleRepository.save(Article.of(LocalDate.now(), "article2", "content", false,
            3, ContentCategory.MEAL));
        Article article3 = articleRepository.save(Article.of(LocalDate.now(), "article3", "content", false,
            3, ContentCategory.MEAL));
        article3.recoverableDelete();

        //when
        Optional<Article> optionalArticle2 = articleRepository.findEntityGraphArticleMatchConditionsByApiIdAndIsDeletedIsFalse(
            article2.getApiId());

        Optional<Article> optionalArticle3 = articleRepository.findEntityGraphArticleMatchConditionsByApiIdAndIsDeletedIsFalse(
            article3.getApiId());

        //then
        assertThat(optionalArticle2.orElseGet(null))
            .isNotNull()
            .extracting(Article::getApiId)
            .isEqualTo(article2.getApiId());
        assertThat(optionalArticle3.isPresent()).isFalse();
    }

}