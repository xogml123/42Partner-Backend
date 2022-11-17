package partner42.moduleapi.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleOnlyIdResponse {

    @Schema(name = "articleId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "게시글 ID")
    private String articleId;

    public static ArticleOnlyIdResponse of(String articleId) {
        return ArticleOnlyIdResponse.builder()
            .articleId(articleId)
            .build();
    }
}