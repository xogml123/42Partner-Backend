package partner42.moduleapi.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import partner42.modulecommon.domain.model.user.RoleEnum;

@Getter
@Setter
public class LoginResponseDto {

    private String userId;
    private List<RoleEnum> role;
}
