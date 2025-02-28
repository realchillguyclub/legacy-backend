package server.poptato.todo.api.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CheckYesterdayTodosRequestDto(
        @NotNull(message = "어제 한 일 체크 시, 할 일 리스트는 필수입니다.")
        List<Long> todoIds
){
}
