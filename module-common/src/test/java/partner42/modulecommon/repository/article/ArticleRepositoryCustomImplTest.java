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
        Article articleFirstCreated = Article.of(LocalDate.now().plusDays(1L), "articleFirstCreated", "content", false,
            3, ContentCategory.MEAL);
        articleRepository.save(articleFirstCreated);

        Article articleIsCompleteTrue = Article.of(LocalDate.now().plusDays(1L), "articleIsCompleteTrue", "content", false,
            3, ContentCategory.MEAL);
        articleIsCompleteTrue.complete();
        articleRepository.save(articleIsCompleteTrue);


        Article articleDeleted = Article.of(LocalDate.now().plusDays(1L), "articleDeleted", "content", false,
            3, ContentCategory.MEAL);
        articleDeleted.recoverableDelete();
        articleRepository.save(articleDeleted);


        Article articleAnonymity = Article.of(LocalDate.now().plusDays(1L), "articleAnonymity", "content", true,
            3, ContentCategory.MEAL);
        articleRepository.save(articleAnonymity);


        Article articleStudy = Article.of(LocalDate.now().plusDays(1L), "articleStudy", "content", false,
            3, ContentCategory.STUDY);
        articleRepository.save(articleStudy);

    }
    @Test
    void givenFiveArticle_whenEachOneArticleSearchConditionExistOrAllEmpty_thenSizeIsEqualsTo() {
        //given

        //when
        Pageable pageable = PageRequest.of(0, 10, Sort.by(List.of(new Order(
            Direction.ASC, "createdAt"))));

        ArticleSearch articleSearchNoProperty = new ArticleSearch();
        Slice<Article> articleSliceNoProperty = articleRepository.findSliceByCondition(
            pageable, articleSearchNoProperty);

        ArticleSearch articleSearchAnonymityTrue = new ArticleSearch();
        articleSearchAnonymityTrue.setAnonymity(true);
        Slice<Article> articleSliceAnonymityTrue = articleRepository.findSliceByCondition(
            pageable, articleSearchAnonymityTrue);


        ArticleSearch articleSearchIsCompleteTrue = new ArticleSearch();
        articleSearchIsCompleteTrue.setIsComplete(true);
        Slice<Article> articleSliceIsCompleteTrue = articleRepository.findSliceByCondition(
            pageable, articleSearchIsCompleteTrue);

        ArticleSearch articleSearchContentCategoryMeal = new ArticleSearch();
        articleSearchContentCategoryMeal.setContentCategory(ContentCategory.MEAL);
        Slice<Article> articleSliceContentCategoryMeal = articleRepository.findSliceByCondition(
            pageable, articleSearchContentCategoryMeal);

        //then
        assertThat(articleSliceNoProperty.getContent().size()).isEqualTo(4);
        assertThat(articleSliceAnonymityTrue.getContent().size()).isEqualTo(1);
        assertThat(articleSliceIsCompleteTrue.getContent().size()).isEqualTo(1);
        assertThat(articleSliceContentCategoryMeal.getContent().size()).isEqualTo(3);

    }

    @Test
    void givenFiveArticle_whenSortByCreatedAtASC_thenEachOneTitleIsEqualTo() {
        //given


        //when
        Pageable pageableSortByCreatedAtAsc = PageRequest.of(0, 10, Sort.by(List.of(new Order(
            Direction.ASC, "createdAt"))));

        ArticleSearch articleSearchNoProperty = new ArticleSearch();
        List<Article> articleListSortByCreatedAtAsc = articleRepository.findSliceByCondition(
            pageableSortByCreatedAtAsc, articleSearchNoProperty).getContent();


        //then
        assertThat(articleListSortByCreatedAtAsc.get(0).getTitle()).isEqualTo("articleFirstCreated");
        assertThat(articleListSortByCreatedAtAsc.get(1).getTitle()).isEqualTo("articleIsCompleteTrue");
        assertThat(articleListSortByCreatedAtAsc.get(2).getTitle()).isEqualTo("articleAnonymity");
        assertThat(articleListSortByCreatedAtAsc.get(3).getTitle()).isEqualTo("articleStudy");

    }

}