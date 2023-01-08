package partner42.moduleapi.service.alarm;


import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import partner42.moduleapi.dto.alarm.AlarmArgsDto;
import partner42.moduleapi.dto.alarm.AlarmDto;
import partner42.modulecommon.domain.model.alarm.Alarm;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.NoEntityException;
import partner42.modulecommon.repository.alarm.AlarmRepository;
import partner42.modulecommon.repository.user.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    public Slice<AlarmDto> findAlarms(Pageable pageable, String username) {
        Member member = userRepository.findByUsername(username)
            .orElseThrow(() -> new NoEntityException(
                ErrorCode.ENTITY_NOT_FOUND)).getMember();
        Slice<Alarm> alarmSlices = alarmRepository.findSliceByCondition(pageable, member.getId());
        return new SliceImpl<>(alarmSlices.getContent().stream()
            .map((alarm) ->
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
}
