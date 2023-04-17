package partner42.moduleapi.dto.alarm;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import partner42.modulecommon.domain.model.alarm.AlarmArgs;
import partner42.modulecommon.domain.model.alarm.AlarmType;
import partner42.modulecommon.domain.model.member.Member;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlarmDto {

    @Schema(name = "alarmId", example = "4f3dda35-3739-406c-ad22-eed438831d66", description = "알림 ID")
    @NotBlank
    private String alarmId;

    @Schema(name = "text", example = "매칭이 확정되었어요!", description = "알림 문구 그대로 보여주면 됨.")
    @NotBlank
    private String text;

    @Schema(name = "alarmArgsDto", example = "json형식 객체", description = "알림 자세한 정보 담은 객체")
    private AlarmArgsDto alarmArgsDto;

    @Schema(name = "isRead", example = "true", description = "알림을 조회 했었는지, ui적으로 다르게 보이게 해주어야함.")
    private Boolean isRead;

}
