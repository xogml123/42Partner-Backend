package com.seoul.openproject.partner.mapper;

import com.seoul.openproject.partner.domain.model.article.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(target="articleId", source = "apiId")
    Article.ArticleOnlyIdResponse entityToArticleOnlyIdResponse(Article article);
}