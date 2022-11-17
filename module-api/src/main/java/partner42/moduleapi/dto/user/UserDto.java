package partner42.moduleapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
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
public class UserDto {
    @Schema(name = "userId" , example = "db688a4a-2f70-4265-a1ea-d15fd6c5c914", description = "사용자 id, 로그인시에 보내지는 값")
    @NotBlank
    private String userId;

    @Schema(name = "oauth2Username" , example = "takim", description = "intraId와 같게 회원가입되어 변경 불가")
    @NotBlank
    private String oauth2Username;

    @Schema(name = "nickname" , example = "꿈꾸는 나무", description = "로그인 할 때 id가 아니라 사용자가 변경할 수 있는 이름, default는 oauth2Username와 같음.")
    @NotBlank
    private String nickname;

    @Schema(name = "email" , example = "takim@student.42seoul.kr", description = "intra email과 같게 회원가입됨.")
    @NotBlank
    private String email;

    @Schema(name = "imageUrl" , example = "https://cdn.intra.42.fr/users/medium_takim.jpg", description = "intra profile image와 같게 회원가입됨.")
    @NotBlank
    private String imageUrl;
}