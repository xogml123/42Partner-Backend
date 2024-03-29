package partner42.modulecommon.domain.model.alarm;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@EqualsAndHashCode
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
