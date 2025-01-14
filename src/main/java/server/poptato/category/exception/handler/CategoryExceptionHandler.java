package server.poptato.category.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import server.poptato.category.exception.CategoryException;
import server.poptato.global.response.BaseErrorResponse;


@Slf4j
@Order(1)
@RestControllerAdvice
public class CategoryExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CategoryException.class)
    public BaseErrorResponse handleCategoryException(CategoryException e) {
        log.error("[CategoryException: handle_CategoryException 호출]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}
