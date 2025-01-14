package server.poptato.category.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import server.poptato.global.response.status.ResponseStatus;

import java.rmi.ServerError;

@RequiredArgsConstructor
public enum CategoryExceptionErrorCode implements ResponseStatus {

    /**
     * 8000: Category 도메인 오류
     */

    DEFAULT_CATEGORY_NOT_EXIST(8000, HttpStatus.INTERNAL_SERVER_ERROR.value(), "기본 카테고리가 존재하지 않습니다."),
    CATEGORY_NOT_EXIST(8001, HttpStatus.BAD_REQUEST.value(), "카테코리가 존재하지 않습니다."),
    CATEGORY_USER_NOT_MATCH(8002, HttpStatus.BAD_REQUEST.value(), "사용자의 카테고리가 아닙니다"),
    INVALID_DRAG_AND_DROP_CATEGORY(8003, HttpStatus.BAD_REQUEST.value(), "전체,중요 카테고리는 순서를 변경할 수 없습니다.");
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
