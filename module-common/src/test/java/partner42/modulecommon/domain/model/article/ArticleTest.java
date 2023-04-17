package partner42.modulecommon.domain.model.article;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.ArticleMatchCondition;
import partner42.modulecommon.domain.model.matchcondition.MatchCondition;
import partner42.modulecommon.domain.model.member.Member;
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
        Article article1 = Article.of(LocalDate.of(2022, 10, 10), "title", "content", false, 3,
            ContentCategory.MEAL);
        List<ArticleMatchCondition> articleMatchConditionsBefore = List.of(
            ArticleMatchCondition.of(null, article1));

        LocalDate changedDate = LocalDate.now();
        String titleChanged = "titleChange";
        String contentChanged = "contentChange";
        ContentCategory changedCC = ContentCategory.STUDY;
        Integer changedParticipantNumMax = 4;
        List<ArticleMatchCondition> changedArticleMatchConditions = List.of(
            ArticleMatchCondition.of(null, article1),
            ArticleMatchCondition.of(null, article1)
        );
        //when
        article1.update(changedDate, titleChanged, contentChanged, true, changedParticipantNumMax,
            changedCC, changedArticleMatchConditions);

        //then
        assertThat(article1)
            .extracting(Article::getDate, Article::getTitle, Article::getContent,
                Article::getAnonymity, Article::getParticipantNumMax, Article::getContentCategory,
                Article::getArticleMatchConditions, Article::getIsDeleted, Article::getIsComplete)
            .containsExactly(changedDate, titleChanged, contentChanged, true,
                changedParticipantNumMax, changedCC, changedArticleMatchConditions, false, false);
        assertThat(article1.getArticleMatchConditions()).hasSize(2);

    }

    @Test
    void update_whenUpdateParticipantMaxNumWithLowValue_thenThrow() {
        //given
        Article article1 = Article.of(LocalDate.of(2022, 10, 10), "title", "content", false, 3,
            ContentCategory.MEAL);
        List<ArticleMatchCondition> articleMatchConditionsBefore = List.of(
            ArticleMatchCondition.of(null, article1));

        LocalDate changedDate = LocalDate.now();
        String titleChanged = "titleChange";
        String contentChanged = "contentChange";
        ContentCategory changedCC = ContentCategory.STUDY;
        Integer changedParticipantNumMax = 4;
        List<ArticleMatchCondition> changedArticleMatchConditions = List.of(
            ArticleMatchCondition.of(null, article1),
            ArticleMatchCondition.of(null, article1)
        );
        //when
        //then
        assertThatThrownBy(() ->
            article1.update(changedDate, titleChanged, contentChanged, true,
                0,
                changedCC, changedArticleMatchConditions))
            .isInstanceOf(InvalidInputException.class);
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
        articleComplete.completeArticleWhenMatchDecided();
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
        articleComplete.completeArticleWhenMatchDecided();
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