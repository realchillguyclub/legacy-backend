package server.poptato.auth.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import server.poptato.global.response.code.BaseErrorCode;
import server.poptato.global.response.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum AuthErrorStatus implements BaseErrorCode {
    _TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "AUTH-001", "토큰이 존재하지 않습니다."),
    _TOKEN_TIME_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-002", "토큰이 만료되었습니다."),
    _INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "토큰이 유효하지 않습니다.")
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
