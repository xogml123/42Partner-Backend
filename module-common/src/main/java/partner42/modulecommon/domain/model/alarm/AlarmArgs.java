package partner42.modulecommon.domain.model.alarm;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlarmArgs {

    // 알람 발생 시킨 멤버
    private String callingMemberNickname;
    // 알람 발생 시킨 글id
    private String articleId;
    // 알람 발생 시킨 댓글id
    private String opinionId;
    //링크 url
    private String linkUrl;
}
