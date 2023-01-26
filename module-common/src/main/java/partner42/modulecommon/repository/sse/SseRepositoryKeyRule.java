package partner42.modulecommon.repository.sse;

import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import partner42.modulecommon.domain.model.alarm.SseEventName;

@RequiredArgsConstructor
@EqualsAndHashCode
public class SseRepositoryKeyRule {
    private static final String UNDER_SCORE = "_";

    private final Long userId;
    private final SseEventName sseEventName;
    private final LocalDateTime createdAt;

    /**
     * SSEInMemoryRepository에서 사용될
     * 특정 user에 대한 특정 브라우저,특정 SSEEventName에
     * 대한 SSEEmitter를 찾기 위한 key를 생성한다.
     * @return
     */
    public String toCompleteKeyWhichSpecifyOnlyOneValue() {
        
        return createdAt != null ? toKeyUserAndEventInfo() + UNDER_SCORE + createdAt.toString() :
            toKeyUserAndEventInfo();
    }

    /**
     * SSEInMemoryRepository에서 사용될
     * 특정 user, 특정 SSEEventName에 대한
     * 모든 SSEEmitter를 찾기 위한 key를 생성한다.
     * @return
     */
    public String toKeyUserAndEventInfo(){
        return userId + UNDER_SCORE + sseEventName.getValue();
    }
}
