package server.poptato.todo.api.request;

import java.util.List;

public record RoutineUpdateRequestDto(
        List<String> routineDays
) {
}
