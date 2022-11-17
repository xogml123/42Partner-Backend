package partner42.moduleapi.dto;

import com.seoul.openproject.partner.domain.model.user.RoleEnum;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {

    private String userId;
    private List<RoleEnum> role;
}
