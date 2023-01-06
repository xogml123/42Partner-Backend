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
public class AccessTokenResponse {

    @Schema(name = "accessToken", example = "JWT token", description = "access token Authorization header에 등록. Bearer +access_token ")
    @NotBlank
    private String accessToken;
}
