package partner42.moduleapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import partner42.moduleapi.dto.article.ArticleOnlyIdResponse;
import partner42.modulecommon.domain.model.article.Article;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(target="articleId", source = "apiId")
    ArticleOnlyIdResponse entityToArticleOnlyIdResponse(Article article);
}