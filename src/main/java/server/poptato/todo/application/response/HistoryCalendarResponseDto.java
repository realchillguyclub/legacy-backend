package server.poptato.todo.application.response;

import java.time.LocalDate;

public record HistoryCalendarResponseDto(
        LocalDate localDate,
        int count
) {

    public static HistoryCalendarResponseDto of(LocalDate localDate, int count) {
        return new HistoryCalendarResponseDto(localDate, count);
    }
}
