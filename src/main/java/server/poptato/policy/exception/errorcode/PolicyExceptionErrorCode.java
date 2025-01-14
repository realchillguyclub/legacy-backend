package server.poptato.policy.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import server.poptato.global.response.status.ResponseStatus;

@RequiredArgsConstructor
public enum PolicyExceptionErrorCode implements ResponseStatus {

    /**
     * 7000: Policy 도메인 오류
     */

    POLICY_NOT_FOUND_EXCEPTION(7000, HttpStatus.NOT_FOUND.value(), "개인정보처리방침이 존재하지 않습니다");


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