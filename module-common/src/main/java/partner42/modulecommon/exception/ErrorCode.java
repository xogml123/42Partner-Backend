package partner42.modulecommon.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "C001", " 잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "C002", " 요청메서드가 허용되지 않습니다."),

    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "C003", " Entity가 존재하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "C004", "서버에서 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST.value(), "C005", " 잘못된 타입입니다."),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "C006", "권한이 없습니다."),

    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST.value(), "C007", "Json형식과 맞지 않습니다."),

    //Authentication
    ACCESS_TOKEN_EXPIRED(HttpStatus.FORBIDDEN.value(), "AU001", "토큰이 만료되었습니다."),
    REFRESH_TOKEN_NOT_IN_COOKIE(HttpStatus.UNAUTHORIZED.value(), "AU002", "쿠키에 refresh-token이 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(), "AU003", "쿠키에 저장된 refresh-token이 만료되었거나 인증이 불가능합니다."),

    USER_TOKEN_NOT_AVAILABLE(HttpStatus.UNAUTHORIZED.value(), "AU004", "Access token을 재발급 받을 수 없습니다."),


     //InfraException
      //비동기 이기 때문에 예외 발생 시 에러코드를 리턴하지 않고 로그 정도만 남김
    SLACK_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "S001", "Slack Error"),
    SSE_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "S002", "SSE Send Error"),



//    // Member
//    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST.value(), "M001", "Email is Duplication"),
//    LOGIN_INPUT_INVALID(HttpStatus.BAD_REQUEST.value(), "M002", "Login input is invalid"),

    // Article
    NOT_ARTICLE_AUTHOR(HttpStatus.FORBIDDEN.value(), "AR001", "게시글의 작성자가 아닙니다."),

    //요청의 처리가 불가능한 경우 -> 방 참여자수가 이미 다 차잇는데 참여를 누르는 경우.
    CANNOT_PARTICIPATE(HttpStatus.CONFLICT.value(), "AR002", "참여가 불가능한 방입니다."),

    NOT_CHANGEABLE_PARTICIPANT_NUM_MAX(HttpStatus.CONFLICT.value(), "AR003", "방의 최대 인원을 변경할 수 없습니다."),
    UNMODIFIABLE_ARTICLE(HttpStatus.CONFLICT.value(), "AR004", "수정할 수 없는 게시글입니다."),
    ALREADY_PARTICIPATED_MEMBER(HttpStatus.CONFLICT.value(), "AR005", "이미 참여한 방입니다."),
    DELETED_ARTICLE(HttpStatus.CONFLICT.value(), "AR006", "삭제된 게시글입니다."),
    COMPLETED_ARTICLE(HttpStatus.CONFLICT.value(), "AR007", "완료된 게시글입니다."),
    FULL_ARTICLE(HttpStatus.CONFLICT.value(), "AR008", "인원이 다 찬 게시글입니다."),
    EMPTY_ARTICLE(HttpStatus.CONFLICT.value(), "AR009", "작성자를 제외한 참여 인원이 없는 게시글입니다."),

    NOT_ALLOW_AUTHOR_MEMBER_DELETE(HttpStatus.CONFLICT.value(), "AR010", "작성자는 참여를 취소할 수 없습니다."),

    ARTICLE_DATE_IS_PAST(HttpStatus.BAD_REQUEST.value(), "AR011", "게시글의 날짜가 과거입니다."),
    //ArticleMember
    NO_AUTHOR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "AR006", "작성자가 없습니다."),
    NOT_PARTICIPATED_MEMBER(HttpStatus.CONFLICT.value(), "AR007", "이 멤버가 참여하지 않은 방입니다."),

    //random Match
    RANDOM_MATCH_ALREADY_EXIST(HttpStatus.CONFLICT.value(), "RM001", "이미 랜덤 매칭에 참여하고 있습니다"),
    ALREADY_CANCELED_RANDOM_MATCH(HttpStatus.CONFLICT.value(), "RM002", "취소할 수 있는 랜덤매칭 신청내역이 존재하지 않습니다."),

    MATCH_CONDITION_EMPTY(HttpStatus.BAD_REQUEST.value(), "RM003", "각각의 필드 별로 매칭 조건을 적어도 하나 선택해야합니다."),

    //Opinion
    NOT_OPINION_AUTHOR(HttpStatus.FORBIDDEN.value(), "OP001", "의견의 작성자가 아닙니다."),

    //User
    NOT_MINE(HttpStatus.FORBIDDEN.value(), "U001", "본인이 아닙니다."),
    //Match
    ALREADY_REVIEWED(HttpStatus.CONFLICT.value(), "MCH001", "이미 리뷰를 작성한 매칭입니다."),
    NOT_MATCH_PARTICIPATED(HttpStatus.FORBIDDEN.value(),"MCH002" ,"참여하지않은 매칭입니다." ),
    REVIEWED_MEMBER_NOT_IN_MATCH(HttpStatus.BAD_REQUEST.value(), "MCH003", "매칭에 참여하지 않은 멤버가 리뷰에 포함되어있습니다."),
    REVIEWING_SELF(HttpStatus.BAD_REQUEST.value(), "MCH004", "자기 자신을 리뷰할 수 없습니다.");

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }


}
