package partner42.moduleapi.controller.alarm;


import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import partner42.moduleapi.dto.alarm.AlarmDto;
import partner42.moduleapi.service.alarm.AlarmService;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @PreAuthorize("hasAuthority('alarm.read')")
    @Operation(summary = "알림 목록조회", description = "조회 시 날짜 기준 내림차순 정렬 다음 쿼리 파라미터 필요.  ?sort=createdDate,desc")
    @GetMapping("/alarms")
    public Slice<AlarmDto> readAllArticle(@ApiParam(hidden = true) @AuthenticationPrincipal UserDetails user,
        Pageable pageable) {
        return alarmService.sendAlarmSliceAndIsReadToTrue(pageable, user.getUsername());
    }

    @PreAuthorize("hasAuthority('alarm.read')")
    @Operation(summary = "알람 sse 구독", description = "알람 sse 구독, sseEmitter객체 반환.")
    @GetMapping(value = "/alarm/subscribe", produces = "text/event-stream")
    public SseEmitter alarmSubscribe(
        @ApiParam(hidden = true) @AuthenticationPrincipal UserDetails user) {
        return alarmService.subscribe(user.getUsername());
    }
}
