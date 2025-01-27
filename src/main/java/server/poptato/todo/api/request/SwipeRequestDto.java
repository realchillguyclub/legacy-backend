package server.poptato.todo.api.request;

import jakarta.validation.constraints.NotNull;

public record SwipeRequestDto(
        @NotNull(message = "할일 ID는 필수입니다.")
        Long todoId
) {
}
