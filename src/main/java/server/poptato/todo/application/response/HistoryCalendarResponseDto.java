package server.poptato.todo.application.response;

import java.time.LocalDate;

public record HistoryCalendarResponseDto(
        LocalDate date,
        int count
) {

    public static HistoryCalendarResponseDto of(LocalDate date, int count) {
        return new HistoryCalendarResponseDto(date, count);
    }
}
