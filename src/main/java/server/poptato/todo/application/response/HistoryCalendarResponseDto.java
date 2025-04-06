package server.poptato.todo.application.response;

import java.time.LocalDate;

public record HistoryCalendarResponseDto(
        LocalDate localDate,
        int count
) {
}
