package partner42.modulecommon.exception;

import com.fasterxml.jackson.annotation.JsonFormat;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", " 잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(405, "C002", " 요청메서드가 허용되지 않습니다."),

    ENTITY_NOT_FOUND(404, "C003", " Entity가 존재하지 않습니다."),
    INTERNAL_SERVER_ERROR(500, "C004", "서버에서 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(400, "C005", " 잘못된 타입입니다."),
    HANDLE_ACCESS_DENIED(403, "C006", "권한이 없습니다."),

    INVALID_JSON_FORMAT(400, "C007", "Json형식과 맞지 않습니다."),

//    //InfraException
      //비동기 이기 때문에 예외 발생 시 에러코드를 리턴하지 않고 로그 정도만 남김
    SLACK_ERROR(500, "S001", "Slack Error"),


//    // Member
//    EMAIL_DUPLICATION(400, "M001", "Email is Duplication"),
//    LOGIN_INPUT_INVALID(400, "M002", "Login input is invalid"),

    // Article
    NOT_ARTICLE_AUTHOR(403, "AR001", "게시글의 작성자가 아닙니다."),

    //요청의 처리가 불가능한 경우 -> 방 참여자수가 이미 다 차잇는데 참여를 누르는 경우.
    NOT_PARTICIPATABLE(409, "AR002", "참여가 불가능한 방입니다."),

    NOT_CHANGEABLE_PARTICIPANT_NUM_MAX(409, "AR003", "방의 최대 인원을 변경할 수 없습니다."),
    UNMODIFIABLE_ARTICLE(409, "AR004", "수정할 수 없는 게시글입니다."),
    ALREADY_PARTICIPATED(409, "AR005", "이미 참여한 방입니다."),
    DELETED_ARTICLE(409, "AR006", "삭제된 게시글입니다."),
    COMPLETED_ARTICLE(409, "AR007", "완료된 게시글입니다."),
    FULL_ARTICLE(409, "AR008", "인원이 다 찬 게시글입니다."),
    EMPTY_ARTICLE(409, "AR009", "작성자를 제외한 참여 인원이 없는 게시글입니다."),
    //ArticleMember
    NO_AUTHOR(500, "AR006", "작성자가 없습니다."),
    NOT_PARTICIPATED_MEMBER(409, "AR007", "이 멤버가 참여하지 않은 방입니다."),

    //random Match
    RANDOM_MATCH_ALREADY_EXIST(409, "RM001", "이미 랜덤 매칭에 참여하고 있습니다");
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
