package server.poptato.emoji.exception;


import lombok.Getter;
import server.poptato.global.response.status.ResponseStatus;

@Getter
public class EmojiException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public EmojiException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
