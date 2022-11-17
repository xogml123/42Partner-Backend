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
public class UserUpdateRequest {

    @Schema(name = "email" , example = "takim@student.42seoul.kr", description = "slack에 등록된 이메일로 변경")
    @NotBlank
    private String email;
}