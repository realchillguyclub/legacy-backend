package server.poptato.auth.exception;

import lombok.Getter;
import server.poptato.global.response.status.ResponseStatus;

@Getter
public class AuthException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public AuthException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
