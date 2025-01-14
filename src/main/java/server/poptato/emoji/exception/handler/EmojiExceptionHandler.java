package server.poptato.emoji.exception.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import server.poptato.emoji.exception.EmojiException;
import server.poptato.global.response.BaseErrorResponse;

@Slf4j
@Order(1)
@RestControllerAdvice
public class EmojiExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmojiException.class)
    public BaseErrorResponse handleTodoException(EmojiException e) {
        log.error("[EmojiException: handle_EmojiException 호출]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}
