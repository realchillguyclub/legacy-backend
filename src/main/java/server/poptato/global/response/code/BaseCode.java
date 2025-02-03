package server.poptato.global.response.code;

import server.poptato.global.response.dto.ReasonDto;

public interface BaseCode {
    public ReasonDto getReason();

    public ReasonDto getReasonHttpStatus();
}
