package server.poptato.emoji.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import server.poptato.global.response.status.ResponseStatus;

@RequiredArgsConstructor
public enum EmojiExceptionErrorCode implements ResponseStatus {

    /**
     * 7000: Emoji 도메인 오류
     */

    EMOJI_NOT_EXIST(7000,HttpStatus.BAD_REQUEST.value(), "존재하지 않는 이모지입니다.");
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
