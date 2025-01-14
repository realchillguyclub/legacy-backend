package server.poptato.global.exception;

import lombok.Getter;
import server.poptato.global.response.status.ResponseStatus;

@Getter
public class BaseException extends RuntimeException{
    private final ResponseStatus exceptionStatus;


    public BaseException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public BaseException(String exceptionMessage, ResponseStatus exceptionStatus) {
        super(exceptionMessage);
        this.exceptionStatus = exceptionStatus;
    }
}
