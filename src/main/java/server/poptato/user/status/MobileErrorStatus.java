package server.poptato.user.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import server.poptato.global.response.code.BaseErrorCode;
import server.poptato.global.response.dto.ErrorReasonDto;

@Getter
@RequiredArgsConstructor
public enum MobileErrorStatus implements BaseErrorCode {
    _NOT_EXIST_FCM_TOKEN(HttpStatus.BAD_REQUEST, "FCM-001", "존재하지 않는 FCM토큰입니다.")
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
