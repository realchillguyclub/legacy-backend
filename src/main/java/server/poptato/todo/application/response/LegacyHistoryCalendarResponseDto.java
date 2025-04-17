package server.poptato.todo.application.response;

import java.time.LocalDate;
import java.util.List;

public record LegacyHistoryCalendarResponseDto(
        List<LocalDate> dates
) {
    public static LegacyHistoryCalendarResponseDto of(List<LocalDate> dates) {
        return new LegacyHistoryCalendarResponseDto(dates);
    }
}
