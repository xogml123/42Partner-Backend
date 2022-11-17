package partner42.moduleapi.dto.opinion;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
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
public class OpinionResponse {

    @Schema(name= "opinionId" , example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "댓글 id")
    @NotBlank
    private String opinionId;

    @Schema(name= "userId" , example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "댓글쓴 사람 id")
    @NotBlank
    private String userId;

    @Schema(name= "nickname" , example = "takim", description = "작성자 이름")
    @NotBlank
    private String nickname;

    @Schema(name= "createdAt" , example = "", description = "작성 시간")
    private LocalDateTime createdAt;

    @Schema(name= "updatedAt" , example = "", description = "수정 시간")
    private LocalDateTime updatedAt;

    @Schema(name= "content" , example = "서초 클러스터 2시에 치킨 먹으러갈겁니다.", description = "댓글 본문")
    @NotNull
    @Size(min = 1, max = 1000)
    private String content;

    @Schema(name= "parentId" , example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "부모 댓글의 id")
    @NotBlank
    private String parentId;

    @Schema(name= "level" , example = "1 or 2", description = "첫 댓글이 1이고 대댓글이 2")
    @NotNull
    private Integer level;
}
