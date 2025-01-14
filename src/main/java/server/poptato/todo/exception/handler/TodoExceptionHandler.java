package server.poptato.todo.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import server.poptato.global.response.BaseErrorResponse;
import server.poptato.todo.exception.TodoException;

@Slf4j
@Order(1)
@RestControllerAdvice
public class TodoExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TodoException.class)
    public BaseErrorResponse handleTodoException(TodoException e) {
        log.error("[UserException: handle_TodoException 호출]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}