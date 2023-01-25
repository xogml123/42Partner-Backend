package partner42.moduleapi.service.alarm;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import partner42.moduleapi.dto.alarm.AlarmArgsDto;
import partner42.moduleapi.dto.alarm.AlarmDto;
import partner42.modulecommon.domain.model.alarm.Alarm;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.user.User;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.repository.alarm.AlarmRepository;
import partner42.modulecommon.repository.sse.SSERepository;
import partner42.modulecommon.repository.user.UserRepository;
import partner42.modulecommon.utils.CustomTimeUtils;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AlarmService {

    @Value("${sse.timeout}")
    private String sseTimeout;
    private static final String SSE_EVENT_ALARM_LIST = "alarmList";


    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    private final SSERepository sseRepository;

    @Transactional
    public Slice<AlarmDto> sendAlarmSliceAndIsReadToTrue(Pageable pageable, String username) {
        Member member = getUserByUsernameOrException(username).getMember();
        Slice<Alarm> alarmSlices = alarmRepository.findSliceByCondition(pageable, member.getId());
        List<Alarm> alarms = alarmSlices.getContent();

        //update 쿼리 여러번 나가는지 확인 해봐야함.
        alarms.forEach(Alarm::read);
        return new SliceImpl<>(alarms.stream()
            .map(alarm ->
                AlarmDto.builder()
                    .alarmId(alarm.getApiId())
                    .text(alarm.getAlarmType().getAlarmContent())
                    .alarmArgsDto(AlarmArgsDto.builder()
                        .articleId(alarm.getAlarmArgs().getArticleId())
                        .opinionId(alarm.getAlarmArgs().getOpinionId())
                        .callingMemberId(alarm.getAlarmArgs().getCallingMemberId())
                        .build())
                    .build())
            .collect(Collectors.toList()),
            alarmSlices.getPageable(), alarmSlices.hasNext());
    }

    public SseEmitter subscribe(String username, String lastEventId) {
        User user = getUserByUsernameOrException(username);
        LocalDateTime now = CustomTimeUtils.nowWithoutNano();
        SseEmitter sse = new SseEmitter(Long.parseLong(sseTimeout));
        sse.onCompletion(() -> {
            log.info("onCompletion callback");
            //만료 시 Repository에서 삭제 되어야함.
            sseRepository.remove(user, now, SSE_EVENT_ALARM_LIST);
        });
        sse.onTimeout(() -> {
            log.info("onTimeout callback");
            sse.complete();
        });
        sseRepository.put(user, now, SSE_EVENT_ALARM_LIST, sse);
        try {
            sse.send(SseEmitter.event()
                .name(SSE_EVENT_ALARM_LIST)
                .id(user.getId() + "_" + SSE_EVENT_ALARM_LIST+ "_" +now)
                .data("subscribe"));
        } catch (IOException e) {
            throw new RuntimeException();
        }

        if (lastEventId != null) {
            LocalDateTime lastEventTime = LocalDateTime.parse(lastEventId.split("_")[2]);
            sseRepository.getKeyList(user, SSE_EVENT_ALARM_LIST).stream()
                .filter(key ->
                    LocalDateTime.parse(key.split("_")[2]).isAfter(lastEventTime)
                )
                .forEach(key -> {
                    SseEmitter sseEmitter = sseRepository.get(user, now, SSE_EVENT_ALARM_LIST);
                    try {
                        sseEmitter.send(SseEmitter.event()
                            .name(SSE_EVENT_ALARM_LIST)
                            .id(key)
                            .data("alarmList"));
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                });
        }
        return sse;
    }



    private User getUserByUsernameOrException(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(
                ErrorCode.ENTITY_NOT_FOUND));
    }


}
