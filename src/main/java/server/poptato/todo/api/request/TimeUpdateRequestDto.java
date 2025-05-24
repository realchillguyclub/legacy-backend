package server.poptato.todo.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public record TimeUpdateRequestDto(
        @JsonFormat(pattern = "HH:mm")
        LocalTime todoTime
) {
}
