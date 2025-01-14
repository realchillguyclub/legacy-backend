package server.poptato.todo.exception;

import lombok.Getter;
import server.poptato.global.response.status.ResponseStatus;

@Getter
public class TodoException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public TodoException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
