package server.poptato.todo.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import server.poptato.global.response.code.BaseErrorCode;
import server.poptato.global.response.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum TodoErrorStatus implements BaseErrorCode {
    _INVALID_PAGE(HttpStatus.BAD_REQUEST, "TODO-001", "유효하지 않은 페이지 수입니다."),
    _TODO_NOT_EXIST(HttpStatus.BAD_REQUEST, "TODO-002", "투두가 존재하지 않습니다"),
    _TODO_USER_NOT_MATCH(HttpStatus.BAD_REQUEST, "TODO-003", "사용자의 할 일이 아닙니다."),
    _ALREADY_COMPLETED_TODO(HttpStatus.BAD_REQUEST, "TODO-004", "달성된 할 일은 스와이프할 수 없습니다."),
    _TODO_TYPE_NOT_MATCH(HttpStatus.BAD_REQUEST, "TODO-005", "드래그앤드롭 시 할 일 리스트와 할 일 타입이 맞지 않습니다." ),
    _BACKLOG_CANT_COMPLETE(HttpStatus.BAD_REQUEST, "TODO-006","백로그 할 일은 달성할 수 없습니다."),
    _YESTERDAY_CANT_COMPLETE(HttpStatus.BAD_REQUEST, "TODO-007","이미 달성한 어제 한 일은 취소할 수 없습니다."),
    _COMPLETED_DATETIME_NOT_EXIST(HttpStatus.BAD_REQUEST, "TODO-008", "존재하지 않는 달성 시각입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .isSuccess(false)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}
