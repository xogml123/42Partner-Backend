package com.seoul.openproject.partner.service.article;

import com.seoul.openproject.partner.domain.model.article.Article;
import com.seoul.openproject.partner.domain.model.article.Article.ArticleOnlyIdResponse;
import com.seoul.openproject.partner.domain.repository.article.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional
    public ArticleOnlyIdResponse createArticle(Article.ArticleDto articleRequest) {

        return null;
    }
}
