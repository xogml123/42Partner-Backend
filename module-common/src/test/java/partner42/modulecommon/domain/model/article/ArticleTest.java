package partner42.modulecommon.domain.model.article;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.ArticleMatchCondition;

class ArticleTest {

    @Test
    void of() {
        //given
        LocalDate localDate = LocalDate.of(2022, 10, 10);
        //when
        Article article = Article.of(localDate, "title", "content", false, 3, ContentCategory.MEAL);
        //then
        assertAll(
            () -> assertEquals(3, article.getParticipantNumMax()),
            () -> assertFalse(article.getIsComplete()),
            () -> assertFalse(article.getIsDeleted()),
            () -> assertEquals(1, article.getParticipantNum()),
            () -> assertEquals(localDate, article.getDate()),
            () -> assertEquals("title", article.getTitle()),
            () -> assertEquals("content", article.getContent()),
            () -> assertFalse(article.getAnonymity()),
            () -> assertEquals(3, article.getParticipantNumMax()),
            () -> assertEquals(ContentCategory.MEAL, article.getContentCategory())
        );

    }

    public void update(LocalDate date, String title, String content, Integer participantNumMax,
        List<ArticleMatchCondition> articleMatchConditions) {
        verifyDeleted();
        verifyCompleted();
        verifyChangeableParticipantNumMax(participantNumMax);
        this.date = date;
        this.title = title;
        this.content = content;
        this.participantNumMax = participantNumMax;
        for (ArticleMatchCondition articleMatchCondition : articleMatchConditions) {
            articleMatchCondition.setArticle(this);
        }
    }
    @Test
    void update() {
        //given
        LocalDate localDate = LocalDate.of(2022, 10, 10);
        Article article1 = Article.of(localDate, "title", "content", false, 3, ContentCategory.MEAL);
        String title = "titleChange";
        String content = "contentChange";
        Integer participantNumMax =

            List<ArticleMatchCondition> articleMatchConditions
        //then
        article.update()
    }

    @Test
    void getAuthorMember() {
    }

    @Test
    void getParticipatedMembers() {
    }

    @Test
    void isDateToday() {
    }

    @Test
    void complete() {
    }

    @Test
    void participateMember() {
    }

    @Test
    void participateCancelMember() {
    }

    @Test
    void recoverableDelete() {
    }
}