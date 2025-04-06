package server.poptato.todo.application.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record HistoryCalendarListResponseDto(
        List<HistoryCalendarResponseDto> historyCalendarList
) {

    public static HistoryCalendarListResponseDto of(Map<LocalDate, Integer> dates) {
        List<HistoryCalendarResponseDto> historyCalendarList = dates.entrySet().stream()
                .map(entry -> new HistoryCalendarResponseDto(entry.getKey(), entry.getValue()))
                .toList();

        return new HistoryCalendarListResponseDto(historyCalendarList);
    }
}