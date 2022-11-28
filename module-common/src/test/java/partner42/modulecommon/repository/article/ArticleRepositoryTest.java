package partner42.modulecommon.repository.article;

import static org.junit.jupiter.api.Assertions.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.utils.CreateUserUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CreateUserUtils createUserUtils;

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
    void findDistinctFetchArticleMatchConditionsByApiIdAndIsDeletedIsFalse() {
        //given
        Article article2 = articleRepository.save(Article.of(LocalDate.now(), "title", "content", false,
            3, ContentCategory.MEAL));
        Article article3 = articleRepository.save(Article.of(LocalDate.now(), "title", "content", false,
            3, ContentCategory.MEAL));
        article3.recoverableDelete();

        //when
        Optional<Article> optionalArticle2 = articleRepository.findDistinctFetchArticleMatchConditionsByApiIdAndIsDeletedIsFalse(
            article2.getApiId());

        Optional<Article> optionalArticle3 = articleRepository.findDistinctFetchArticleMatchConditionsByApiIdAndIsDeletedIsFalse(
            article3.getApiId());

        //then
        /**
         *
         */

    }

    @Test
    void findDistinctFetchArticleMembersByApiIdAndIsDeletedIsFalse() {

        //given
        Article article2 = articleRepository.save(Article.of(LocalDate.now(), "title", "content", false,
            3, ContentCategory.MEAL));
        Article article3 = articleRepository.save(Article.of(LocalDate.now(), "title", "content", false,
            3, ContentCategory.MEAL));
        article3.recoverableDelete();

        //when
        Optional<Article> optionalArticle2 = articleRepository.findDistinctFetchArticleMembersByApiIdAndIsDeletedIsFalse(
            article2.getApiId());

        Optional<Article> optionalArticle3 = articleRepository.findDistinctFetchArticleMembersByApiIdAndIsDeletedIsFalse(
            article3.getApiId());

        //then
        assertEquals(article2, optionalArticle2.orElseGet(() -> null));

        assertFalse(optionalArticle3.isPresent());
    }
}