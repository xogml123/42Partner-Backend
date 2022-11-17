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
public class UserOnlyIdResponse {
    @Schema(name = "userId" , example = "db688a4a-2f70-4265-a1ea-d15fd6c5c914", description = "사용자 id, 로그인시에 보내지는 값")
    @NotBlank
    private String userId;
}