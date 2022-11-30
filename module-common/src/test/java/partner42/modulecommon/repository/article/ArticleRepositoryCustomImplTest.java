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
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
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
    UserRepository userRepository;
    @Autowired
    CreateTestDataUtils createTestDataUtils;
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ArticleMemberRepository articleMemberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        createTestDataUtils.signUpUsers();
        Article article1 = Article.of(LocalDate.now().plusDays(1L), "title", "content", false,
            3, ContentCategory.MEAL);
        articleRepository.save(article1);

        articleMemberRepository.save(
            ArticleMember.of(memberRepository.findByNickname("takim").get(), true, article1));

        Article articleAnonymity = Article.of(LocalDate.now().plusDays(1L), "title", "content", true,
            3, ContentCategory.MEAL);
        articleRepository.save(articleAnonymity);

        articleMemberRepository.save(
            ArticleMember.of(memberRepository.findByNickname("takim").get(), true, articleAnonymity));

        Article articleMatched = Article.of(LocalDate.now().plusDays(1L), "title", "content", false,
            3, ContentCategory.MEAL);
        articleRepository.save(articleMatched);
        articleMatched.complete();

        articleMemberRepository.save(
            ArticleMember.of(memberRepository.findByNickname("takim").get(), true, articleMatched));

        Article articleStudy = Article.of(LocalDate.now().plusDays(1L), "title", "content", false,
            3, ContentCategory.STUDY);
        articleRepository.save(articleStudy);
        articleMemberRepository.save(
            ArticleMember.of(memberRepository.findByNickname("takim").get(), true, articleStudy));
    }
    @Test
    @DisplayName("findSliceByCondition")
    void findSliceByCondition() {
        //given

        //when
        //anonymity=true인 경우
        ArticleSearch articleSearch = null;
        articleSearch = new ArticleSearch();
        articleSearch.setAnonymity(true);
        Slice<Article> articleSliceAnonymity = articleRepository.findSliceByCondition(
            PageRequest.of(0, 10, Sort.by(List.of(new Order(
                Direction.DESC, "createdAt")))), articleSearch);
        //isComplete=true인 경우
        articleSearch = new ArticleSearch();
        articleSearch.setIsComplete(true);
        Slice<Article> articleSliceComplete = articleRepository.findSliceByCondition(
            PageRequest.of(0, 10, Sort.by(List.of(new Order(
                Direction.DESC, "createdAt")))), articleSearch);
        //contentCategory=STUDY인 경우
        articleSearch = new ArticleSearch();
        articleSearch.setContentCategory(ContentCategory.STUDY);
        Slice<Article> articleSliceStudy = articleRepository.findSliceByCondition(
            PageRequest.of(0, 10, Sort.by(List.of(new Order(
                Direction.DESC, "createdAt")))), articleSearch);

        articleSearch = new ArticleSearch();
        Slice<Article> articleSlice= articleRepository.findSliceByCondition(
            PageRequest.of(0, 10, Sort.by(List.of(new Order(
                Direction.DESC, "createdAt")))), articleSearch);

        //then
        assertThat(articleSlice.getContent().size()).isEqualTo(4);
        assertThat(articleSliceAnonymity.getContent().size()).isEqualTo(1);
        assertThat(articleSliceComplete.getContent().size()).isEqualTo(1);
        assertThat(articleSliceStudy.getContent().size()).isEqualTo(1);
    }
}