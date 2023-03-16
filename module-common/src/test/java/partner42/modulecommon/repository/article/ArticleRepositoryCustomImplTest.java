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
    void findSliceByCondition_givenArticleWithDifferentArticleSearchProperty_whenArticleSearchDiverse_thenFilterByArticleSearch() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(List.of(new Order(
            Direction.ASC, "createdAt"))));

        ArticleSearch articleSearchNoProperty = new ArticleSearch();

        ArticleSearch articleSearchAnonymityTrue = new ArticleSearch();
        articleSearchAnonymityTrue.setAnonymity(true);


        ArticleSearch articleSearchIsCompleteTrue = new ArticleSearch();
        articleSearchIsCompleteTrue.setIsComplete(true);

        ArticleSearch articleSearchContentCategoryMeal = new ArticleSearch();
        articleSearchContentCategoryMeal.setContentCategory(ContentCategory.MEAL);
        //when

        Slice<Article> articleSliceNoProperty = articleRepository.findSliceByCondition(
            pageable, articleSearchNoProperty);
        Slice<Article> articleSliceAnonymityTrue = articleRepository.findSliceByCondition(
            pageable, articleSearchAnonymityTrue);
        Slice<Article> articleSliceIsCompleteTrue = articleRepository.findSliceByCondition(
            pageable, articleSearchIsCompleteTrue);
        Slice<Article> articleSliceContentCategoryMeal = articleRepository.findSliceByCondition(
            pageable, articleSearchContentCategoryMeal);
        //then
        assertThat(articleSliceNoProperty.getContent()).hasSize(4);
        assertThat(articleSliceAnonymityTrue.getContent()).hasSize(1);
        assertThat(articleSliceIsCompleteTrue.getContent()).hasSize(1);
        assertThat(articleSliceContentCategoryMeal.getContent()).hasSize(3);

    }

    @Test
    void findSliceByCondition_givenArticleWithDifferentCreatedAt_whenSortByCreatedAtASC_thenExactlyExpectedOrder() {
        //given
        Sort sort = Sort.by(List.of(new Order(
            Direction.ASC, "createdAt")));
        Pageable pageableSortByCreatedAtAsc = PageRequest.of(0, 10, sort);
        ArticleSearch articleSearchNoProperty = new ArticleSearch();
        //when

        List<Article> articleListSortByCreatedAtAsc = articleRepository.findSliceByCondition(
            pageableSortByCreatedAtAsc, articleSearchNoProperty).getContent();

        //then
        assertThat(articleListSortByCreatedAtAsc)
            .extracting(Article::getTitle)
            .containsExactly("articleFirstCreated", "articleIsCompleteTrue", "articleAnonymity", "articleStudy");
    }

    @Test
    void findSliceByCondition_givenArticles_whenPageSizeNearEntireSize_thenNextFlag() {
        //given
        Sort sort = Sort.by(List.of(new Order(
            Direction.ASC, "createdAt")));
        Pageable pageLower = PageRequest.of(0, 3, sort);
        Pageable pageEqual = PageRequest.of(0, 4, sort);
        Pageable pageHigher = PageRequest.of(0, 5, sort);

        ArticleSearch articleSearchNoProperty = new ArticleSearch();
        //when

        Slice<Article> articleSliceWithLowerPageSize = articleRepository.findSliceByCondition(
            pageLower, articleSearchNoProperty);

        Slice<Article> articleSliceWithEqualPageSize = articleRepository.findSliceByCondition(
            pageEqual, articleSearchNoProperty);

        Slice<Article> articleSliceWithHigherPageSize = articleRepository.findSliceByCondition(
            pageHigher, articleSearchNoProperty);

        //then
        assertThat(articleSliceWithLowerPageSize.hasNext()).isTrue();
        assertThat(articleSliceWithEqualPageSize.hasNext()).isFalse();
        assertThat(articleSliceWithHigherPageSize.hasNext()).isFalse();
    }

}