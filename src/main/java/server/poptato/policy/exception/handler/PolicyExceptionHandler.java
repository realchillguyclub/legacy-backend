package server.poptato.policy.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import server.poptato.global.response.BaseErrorResponse;
import server.poptato.policy.exception.PolicyException;

@Slf4j
@Order(1)
@RestControllerAdvice
public class PolicyExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PolicyException.class)
    public BaseErrorResponse handlePolicyException(PolicyException e) {
        log.error("[PolicyException: handle_PolicyException 호출]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}