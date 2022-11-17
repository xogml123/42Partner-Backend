package partner42.modulecommon.repository.article;

import partner42.modulecommon.domain.model.match.ContentCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleSearch {

    @Schema(name = "isMatched", example = "true or false ", description = "매칭된 글만 조회할건지 아직 매치 되지않을걸 조회할건지, 값을 지정안하면 모두 가져옴.")
    private Boolean isMatched;

    @Schema(name = "contentCategory", example = "MEAL or STUDY", description = "식사, 공부 글인지 여부, 값을 꼭 지정해야함.")
    private ContentCategory contentCategory;
}
