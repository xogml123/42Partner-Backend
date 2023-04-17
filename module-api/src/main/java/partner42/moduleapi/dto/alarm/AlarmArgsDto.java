package partner42.moduleapi.dto.alarm;


import io.swagger.v3.oas.annotations.media.Schema;
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
public class AlarmArgsDto {

    @Schema(name = "callingMemberNickname", example = "takim", description = "알림을 일으킨 주체의 Nickname")
    private String callingMemberNickname;

    @Schema(name = "articleId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "알림 일어난 작성 글 ID")
    private String articleId;

    @Schema(name = "opinionId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "새로 남겨진 댓글 ID")
    private String opinionId;
}
