package partner42.modulecommon.repository.article;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.article.ArticleMember;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.repository.articlemember.ArticleMemberRepository;
import partner42.modulecommon.repository.member.MemberRepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.utils.CreateTestDataUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleRepositoryCustomImplTest {


    @Autowired
    ArticleRepository articleRepository;


    @BeforeEach
    void setUp() {


    }
    @Test
    void givenFiveArticle_whenEachOneArticleSearchConditionExistOrAllEmpty_thenSizeIsEqualsTo() {
        //given
        Article articleFirstCreated = Article.of(LocalDate.now().plusDays(1L), "title", "content", false,
            3, ContentCategory.MEAL);
        articleRepository.save(articleFirstCreated);

        Article articleIsCompleteTrue = Article.of(LocalDate.now().plusDays(1L), "title", "content", false,
            3, ContentCategory.MEAL);
        articleRepository.save(articleIsCompleteTrue);
        articleIsCompleteTrue.complete();

        Article articleDeleted = Article.of(LocalDate.now().plusDays(1L), "title", "content", false,
            3, ContentCategory.MEAL);
        articleRepository.save(articleDeleted);
        articleDeleted.recoverableDelete();

        Article articleAnonymity = Article.of(LocalDate.now().plusDays(1L), "title", "content", true,
            3, ContentCategory.MEAL);
        articleRepository.save(articleAnonymity);


        Article articleStudy = Article.of(LocalDate.now().plusDays(1L), "title", "content", false,
            3, ContentCategory.STUDY);
        articleRepository.save(articleStudy);

        //when
        Pageable pageable = new PageRequest(0, 10, new Sort())

        ArticleSearch articleSearchNoProperty = new ArticleSearch();
        Slice<Article> articleSliceNoProperty = articleRepository.findSliceByCondition(
            null, articleSearchNoProperty);

        ArticleSearch articleSearchAnonymityTrue = new ArticleSearch();
        articleSearchAnonymityTrue.setAnonymity(true);
        Slice<Article> articleSliceAnonymityTrue = articleRepository.findSliceByCondition(
            null, articleSearchAnonymityTrue);


        ArticleSearch articleSearchIsCompleteTrue = new ArticleSearch();
        articleSearchIsCompleteTrue.setIsComplete(true);
        Slice<Article> articleSliceIsCompleteTrue = articleRepository.findSliceByCondition(
            null, articleSearchIsCompleteTrue);

        ArticleSearch articleSearchContentCategoryMeal = new ArticleSearch();
        articleSearchContentCategoryMeal.setContentCategory(ContentCategory.MEAL);
        Slice<Article> articleSliceContentCategoryMeal = articleRepository.findSliceByCondition(
            null, articleSearchContentCategoryMeal);

        //then
        assertThat(articleSliceNoProperty.getSize()).isEqualTo(4);
        assertThat(articleSliceAnonymityTrue.getSize()).isEqualTo(1);
        assertThat(articleSliceIsCompleteTrue.getSize()).isEqualTo(1);
        assertThat(articleSliceContentCategoryMeal.getSize()).isEqualTo(3);

    }
}