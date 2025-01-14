package server.poptato.policy.exception;

import lombok.Getter;
import server.poptato.global.response.status.ResponseStatus;

@Getter
public class PolicyException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public PolicyException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}