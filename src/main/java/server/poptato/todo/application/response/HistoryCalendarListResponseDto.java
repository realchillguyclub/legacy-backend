package server.poptato.todo.application.response;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public record HistoryCalendarListResponseDto(
        List<HistoryCalendarResponseDto> historyCalendarList
) {

    public static HistoryCalendarListResponseDto from(Map<LocalDate, Integer> dates) {
        List<HistoryCalendarResponseDto> historyCalendarList = dates.entrySet().stream()
                .map(entry -> HistoryCalendarResponseDto.of(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(HistoryCalendarResponseDto::date))
                .toList();

        return new HistoryCalendarListResponseDto(historyCalendarList);
    }
}
