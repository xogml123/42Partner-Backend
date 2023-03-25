package partner42.moduleapi.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class MemberDto {
    @Schema(name = "nickname" , example = "꿈꾸는 나무", description = "글 작성자 혹은 참여자 (member)의 nickname")
    @NotBlank
    private String nickname;

    @Schema(name = "isAuthor" , example = "true Or false", description = "작성자이면 true")
    @NotNull
    private Boolean isAuthor;

    @Schema(name = "isMe" , example = "true Or false", description = "자신이면 true")
    @NotNull
    private Boolean isMe;

}