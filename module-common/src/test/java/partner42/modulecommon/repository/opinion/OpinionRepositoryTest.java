package partner42.modulecommon.repository.opinion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.opinion.Opinion;
import partner42.modulecommon.repository.article.ArticleRepository;
import partner42.modulecommon.repository.member.MemberRepository;

@DataJpaTest
//custom db를 사용하기 위해 필요, 없으면 embeded h2db사용하려고 함.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OpinionRepositoryTest {

    @Autowired
    private OpinionRepository opinionRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    void findAllEntityGraphArticleAndMemberAuthorByArticleApiIdAndIsDeletedIsFalse_given() {
        //given
        Article article1 = Article.of(LocalDate.now(), "article1", "", false, 3, ContentCategory.MEAL);
        Article article2 = Article.of(LocalDate.now(), "article2", "", false, 3, ContentCategory.MEAL);

        articleRepository.saveAll(List.of(article1, article2));

        Member author = Member.of("author");
        memberRepository.save(author);

        Opinion opinion1 = Opinion.of("content", author, article1, null);
        Opinion opinion2 = Opinion.of("content", author, article1, null);
        Opinion opinion3 = Opinion.of("content", author, article1, null);
        Opinion opinion4 = Opinion.of("content", author, article2, null);
        Opinion opinionDeleted = Opinion.of("content", author, article2, null);
        opinionDeleted.recoverableDelete();
        opinionRepository.saveAll(List.of(opinion1, opinion2, opinion3, opinionDeleted, opinion4));

        String falseArticleApiId = "FalseApiId";
        //when

        List<Opinion> opinionList1 = opinionRepository.findAllEntityGraphArticleAndMemberAuthorByArticleApiIdAndIsDeletedIsFalse(
            article1.getApiId());
        List<Opinion> opinionList2 = opinionRepository.findAllEntityGraphArticleAndMemberAuthorByArticleApiIdAndIsDeletedIsFalse(
            article2.getApiId());
        List<Opinion> opinionListWithFalseArticleApiId = opinionRepository.findAllEntityGraphArticleAndMemberAuthorByArticleApiIdAndIsDeletedIsFalse(
            falseArticleApiId);
        //then
        assertThat(opinionList1).hasSize(3);
        assertThat(opinionList2).hasSize(1);
        assertThat(opinionListWithFalseArticleApiId).isEmpty();

    }
}