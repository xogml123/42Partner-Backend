package partner42.moduleapi.dto.random;


import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
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
public class RandomMatchExistDto {

    @Schema(name = "isExist", example = "true or false", description = "랜덤 매칭 신청상태인지 확인")
    @NotNull
    Boolean isExist;

}
