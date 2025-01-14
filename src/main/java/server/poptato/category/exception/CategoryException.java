package server.poptato.category.exception;


import lombok.Getter;
import server.poptato.global.response.status.ResponseStatus;

@Getter
public class CategoryException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public CategoryException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
