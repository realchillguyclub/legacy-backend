package server.poptato.todo.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RoutineUpdateRequestDto(
        @NotNull
        @Size(min = 1, message = "루틴 요일은 최소 1개 이상이어야 합니다.")
        List<String> routineDays
) {
}
