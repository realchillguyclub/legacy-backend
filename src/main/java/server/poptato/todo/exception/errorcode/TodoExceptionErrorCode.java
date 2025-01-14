package server.poptato.todo.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import server.poptato.global.response.status.ResponseStatus;

@RequiredArgsConstructor
public enum TodoExceptionErrorCode implements ResponseStatus {

    /**
     * 5000: Todo 도메인 오류
     */

    INVALID_PAGE(5000, HttpStatus.BAD_REQUEST.value(), "유효햐지 않은 페이지 수입니다."),
    TODO_NOT_EXIST(5001, HttpStatus.BAD_REQUEST.value(), "투두가 존재하지 않습니다"),
    TODO_USER_NOT_MATCH(5002, HttpStatus.BAD_REQUEST.value(), "사용자의 할 일이 아닙니다."),
    ALREADY_COMPLETED_TODO(5003, HttpStatus.BAD_REQUEST.value(), "달성된 할 일은 스와이프할 수 없습니다."),
    TODO_TYPE_NOT_MATCH(5004, HttpStatus.BAD_REQUEST.value(), "드래그앤드롭 시 할 일 리스트와 할 일 타입이 맞지 않습니다." ),
    BACKLOG_CANT_COMPLETE(5005,HttpStatus.BAD_REQUEST.value(), "백로그 할 일은 달성할 수 없습니다."),
    YESTERDAY_CANT_COMPLETE(5006, HttpStatus.BAD_REQUEST.value(), "이미 달성한 어제 한 일은 취소할 수 없습니다."),
    COMPLETED_DATETIME_NOT_EXIST(5007, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 달성 시각입니다.");


    private final int code;
    private final int status;
    private final String message;


    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

