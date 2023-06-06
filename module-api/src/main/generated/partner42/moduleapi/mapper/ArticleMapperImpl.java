package partner42.moduleapi.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import partner42.moduleapi.dto.article.ArticleOnlyIdResponse;
import partner42.modulecommon.domain.model.article.Article;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-04T21:37:11+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.13 (Azul Systems, Inc.)"
)
@Component
public class ArticleMapperImpl implements ArticleMapper {

    @Override
    public ArticleOnlyIdResponse entityToArticleOnlyIdResponse(Article article) {
        if ( article == null ) {
            return null;
        }

        ArticleOnlyIdResponse.ArticleOnlyIdResponseBuilder articleOnlyIdResponse = ArticleOnlyIdResponse.builder();

        articleOnlyIdResponse.articleId( article.getApiId() );

        return articleOnlyIdResponse.build();
    }
}
