package com.seoul.openproject.partner.service.article;

import static org.junit.jupiter.api.Assertions.*;

import com.seoul.openproject.partner.domain.model.article.Article.ArticleDto;
import com.seoul.openproject.partner.domain.model.article.Article.ArticleOnlyIdResponse;
import com.seoul.openproject.partner.domain.model.match.ContentCategory;
import com.seoul.openproject.partner.domain.model.matchcondition.MatchCondition.MatchConditionDto;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArticleServiceTest {

    @Autowired
    private  ArticleService articleService;
    private static final String takimId = "6acb1d9c-f135-4dd8-9c08-be6b4c3c7fee";
    private static final String sorkimId = "6281e9f9-90af-4d44-9e87-a7f83d83b6ab";
    private static final String hyenamId = "bdf8e1ea-4c40-4073-9c68-01ea8c1a0a09";
    private static final String adminId = "f4fba3ae-4bc5-471d-a486-6a7495ae86e8";

    @Test
    void completeArticle() {
        String articleId = articleService.createArticle(takimId, ArticleDto.builder()
            .title("test")
            .date(LocalDate.now())
            .matchConditionDto(MatchConditionDto.of(new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()))
            .content("test")
            .anonymity(false)
            .participantNumMax(4)
            .contentCategory(ContentCategory.MEAL)
            .build()).getArticleId();

        articleService.participateArticle(sorkimId, articleId);
//        articleService.participateArticle(hyenamId, articleId);

        articleService.completeArticle(takimId, articleId);
    }
}