package server.poptato.global.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import server.poptato.global.response.code.BaseErrorCode;
import server.poptato.global.response.dto.ErrorReasonDto;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 전역 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"GLOBAL-500", "서버 내부 오류가 발생했습니다. 자세한 사항은 백엔드 팀에 문의하세요."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"GLOBAL-400", "입력 값이 잘못된 요청 입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"GLOBAL-401", "인증이 필요 합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "GLOBAL-403", "금지된 요청 입니다."),
    _METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "GLOBAL-405", "허용되지 않은 요청 메소드입니다."),
    _UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "GLOBAL-415", "지원되지 않는 미디어 타입입니다."),
    _NOT_FOUND_HANDLER(HttpStatus.NOT_FOUND, "GLOBAL-404", "해당 경로에 대한 핸들러를 찾을 수 없습니다."),
    _FAILED_TRANSLATE_SWAGGER(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL-500", "Rest Docs로 생성된 json파일을 통한 스웨거 변환에 실패하였습니다."),
    _INVALID_HEADER_VALUE(HttpStatus.BAD_REQUEST, "GLOBAL-400", "요청 헤더에 올바르지 않은 값이 포함되어 있습니다.")
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
