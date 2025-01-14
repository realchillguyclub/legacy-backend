package server.poptato.user.exception;

import lombok.Getter;
import server.poptato.global.response.status.ResponseStatus;

@Getter
public class UserException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public UserException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
