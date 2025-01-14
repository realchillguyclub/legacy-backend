package server.poptato.auth.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import server.poptato.global.response.status.ResponseStatus;

@RequiredArgsConstructor
public enum AuthExceptionErrorCode implements ResponseStatus {

    /**
     * 6000: Auth 도메인 오류
     */

    TOKEN_NOT_EXIST(6000, HttpStatus.BAD_REQUEST.value(), "토큰 값이 필요합니다."),
    TOKEN_TIME_EXPIRED(6001, HttpStatus.UNAUTHORIZED.value(), "토큰이 만료되었습니다"),
    INVALID_TOKEN(6002, HttpStatus.BAD_REQUEST.value(), "토큰이 유효하지 않습니다");

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
