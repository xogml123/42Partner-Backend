package partner42.modulecommon.domain.model.alarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {

    PARTICIPATION_ON_MY_POST("내가 쓴 글에 참여가 있었어요!"),
    PARTICIPATION_CANCEL_ON_MY_POST("내가 쓴 글에 참여가 취소되었어요!"),
    MATCH_CONFIRMED("매칭이 확정되었어요!"),
    COMMENT_ON_MY_COMMENT("내가 쓴 댓글에 댓글이 달렸어요!");

    private final String alarmContent;
}
