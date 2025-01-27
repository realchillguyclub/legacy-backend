package server.poptato.category.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import server.poptato.global.response.code.BaseErrorCode;
import server.poptato.global.response.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorStatus implements BaseErrorCode {
    _DEFAULT_CATEGORY_NOT_EXIST(HttpStatus.INTERNAL_SERVER_ERROR, "CATEGORY-001", "기본 카테고리가 존재하지 않습니다."),
    _CATEGORY_NOT_EXIST(HttpStatus.BAD_REQUEST, "CATEGORY-002", "카테고리가 존재하지 않습니다."),
    _CATEGORY_USER_NOT_MATCH(HttpStatus.BAD_REQUEST, "CATEGORY-003", "사용자의 카테고리가 아닙니다"),
    _INVALID_DRAG_AND_DROP_CATEGORY(HttpStatus.BAD_REQUEST, "CATEGORY-004", "전체 & 중요 카테고리는 순서를 변경할 수 없습니다.")
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
