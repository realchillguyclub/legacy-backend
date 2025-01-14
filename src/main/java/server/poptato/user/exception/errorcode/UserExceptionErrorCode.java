package server.poptato.user.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import server.poptato.global.response.status.ResponseStatus;

@RequiredArgsConstructor
public enum UserExceptionErrorCode implements ResponseStatus {

    /**
     * 4000: User 도메인 오류
     */

    USER_NOT_EXIST(4000, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 사용자입니다.");

    private final int code;
    private final int status;
    private final String message;


    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

