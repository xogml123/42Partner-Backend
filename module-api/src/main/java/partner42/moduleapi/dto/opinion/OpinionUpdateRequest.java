package partner42.moduleapi.dto.opinion;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
public class OpinionUpdateRequest{

    @Schema(name= "content" , example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "댓글 본문")
    @NotNull
    @Size(min = 1, max = 1000)
    private String content;

}
