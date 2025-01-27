package server.poptato.todo.application.response;

import java.time.LocalDate;
import java.util.List;

public record HistoryCalendarListResponseDto(
        List<LocalDate> dates
) {

    public static HistoryCalendarListResponseDto of(List<LocalDate> dates) {
        return new HistoryCalendarListResponseDto(dates);
    }
}