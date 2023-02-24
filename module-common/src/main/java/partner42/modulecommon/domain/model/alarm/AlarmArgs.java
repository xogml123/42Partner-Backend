package partner42.modulecommon.domain.model.alarm;


import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AlarmArgs implements Serializable {

    private static final long serialVersionUID = 300L;
    // 알람 발생 시킨 멤버
    private String callingMemberNickname;
    // 알람 발생 시킨 글id
    private String articleId;
    // 알람 발생 시킨 댓글id
    private String opinionId;
    //링크 url
    private String linkUrl;
}
