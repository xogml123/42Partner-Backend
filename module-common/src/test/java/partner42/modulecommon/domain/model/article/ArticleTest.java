package partner42.modulecommon.domain.model.article;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.ArticleMatchCondition;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.InvalidInputException;

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


    @Test
    void update() {
        //given
        LocalDate localDate = LocalDate.of(2022, 10, 10);
        Article article1 = Article.of(localDate, "title", "content", false, 3,
            ContentCategory.MEAL);
        List<ArticleMatchCondition> articleMatchConditionsBefore = List.of(
            ArticleMatchCondition.of(null, article1));
        String title = "titleChange";
        String content = "contentChange";
        Integer participantNumMax = 4;
        List<ArticleMatchCondition> articleMatchConditionsAfter = List.of(
            ArticleMatchCondition.of(null, article1),
            ArticleMatchCondition.of(null, article1)

        );
        //when
        article1.update(localDate, title
            , content, participantNumMax, articleMatchConditionsAfter);
        //then
        assertAll(
            () -> assertEquals(4, article1.getParticipantNumMax()),
            () -> assertFalse(article1.getIsComplete()),
            () -> assertFalse(article1.getIsDeleted()),
            () -> assertEquals(1, article1.getParticipantNum()),
            () -> assertEquals(localDate, article1.getDate()),
            () -> assertEquals(title, article1.getTitle()),
            () -> assertEquals(content, article1.getContent()),
            () -> assertFalse(article1.getAnonymity()),
            () -> assertEquals(4, article1.getParticipantNumMax()),
            () -> assertEquals(ContentCategory.MEAL, article1.getContentCategory()),
            () -> assertEquals(2, article1.getArticleMatchConditions().size())
        );
        assertThatThrownBy(() ->
            article1.update(localDate, title
                , content, 1, articleMatchConditionsAfter));
    }


    @Test
    void participateMember() {
        //given
        LocalDate localDate = LocalDate.of(2022, 10, 10);
        Article article1 = Article.of(localDate, "title", "content", false, 3,
            ContentCategory.MEAL);
        Article articleDelete = Article.of(localDate, "title", "content", false, 3,
            ContentCategory.MEAL);
        Article articleComplete = Article.of(localDate, "title", "content", false, 3,
            ContentCategory.MEAL);
        Article articleParticipatedMember = Article.of(localDate, "title", "content", false, 3,
            ContentCategory.MEAL);
        Article articleOverParticipantNum = Article.of(localDate, "title", "content", false, 2,
            ContentCategory.MEAL);

        Member takim = Member.of("takim");
        Member sorkim = Member.of("sorkim");
        Member dum = Member.of("dum");

        ArticleMember.of(takim, true, article1);
        ArticleMember.of(takim, true, articleDelete);
        ArticleMember.of(takim, true, articleComplete);
        ArticleMember.of(takim, true, articleParticipatedMember);
        ArticleMember.of(takim, true, articleOverParticipantNum);

        //when
        ArticleMember articleMember = article1.participateMember(sorkim);
        articleDelete.recoverableDelete();
        articleComplete.complete();
        articleParticipatedMember.participateMember(sorkim);
        articleOverParticipantNum.participateMember(sorkim);

        //then

        assertAll(
            () -> assertThatThrownBy(() ->
                articleDelete.participateMember(sorkim)),
            () -> assertThatThrownBy(() ->
                articleComplete.participateMember(sorkim)),
            () -> assertThatThrownBy(() ->
                articleParticipatedMember.participateMember(sorkim)),
            () -> assertThat(article1.getParticipatedMembers().size()).isEqualTo(1),
            () -> assertThat(article1.getAuthorMember().getNickname()).isEqualTo(
                takim.getNickname()),
            () -> assertThat(articleMember.getMember().getNickname()).isEqualTo(
                sorkim.getNickname()),
            () -> assertThat(article1.getParticipantNum()).isEqualTo(2),
            () -> assertThatThrownBy(() ->
                articleOverParticipantNum.participateMember(dum))
        );

    }


    @Test
    void participateCancelMember() {


        //given
        LocalDate localDate = LocalDate.of(2022, 10, 10);
        Article articleUnParticipated = Article.of(localDate, "title", "content", false, 3,
            ContentCategory.MEAL);
        Article articleDelete = Article.of(localDate, "title", "content", false, 3,
            ContentCategory.MEAL);
        Article articleComplete = Article.of(localDate, "title", "content", false, 3,
            ContentCategory.MEAL);
        Article articleParticipatedMember = Article.of(localDate, "title", "content", false, 3,
            ContentCategory.MEAL);


        Member takim = Member.of("takim");
        Member sorkim = Member.of("sorkim");
        ArticleMember.of(takim, true, articleUnParticipated);
        ArticleMember.of(takim, true, articleDelete);
        ArticleMember.of(takim, true, articleComplete);
        ArticleMember.of(takim, true, articleParticipatedMember);

        //when
        articleDelete.recoverableDelete();
        articleComplete.complete();
        articleParticipatedMember.participateMember(sorkim);
        ArticleMember articleMember = articleParticipatedMember.participateCancelMember(sorkim);

        //then

        assertAll(
            () -> assertThatThrownBy(() ->
                articleDelete.participateCancelMember(sorkim)),
            () -> assertThatThrownBy(() ->
                articleComplete.participateCancelMember(sorkim)),
            () -> assertThatThrownBy(() ->
                articleUnParticipated.participateCancelMember(sorkim)),
            () -> assertThat(articleParticipatedMember.getParticipatedMembers().size()).isEqualTo(0),
            () -> assertThat(articleParticipatedMember.getAuthorMember().getNickname()).isEqualTo(takim.getNickname()),
            () -> assertThat(articleParticipatedMember.getParticipantNum()).isEqualTo(1),

            () -> assertThat(articleMember.getMember().getNickname()).isEqualTo(sorkim.getNickname())

        );
    }
}