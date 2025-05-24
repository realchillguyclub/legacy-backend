package server.poptato.todo.application.response;

import java.time.LocalDate;
import java.util.List;

public record TodayListResponseDto(
        LocalDate date,
        List<TodayResponseDto> todays,
        int totalPageCount
) {
    public static TodayListResponseDto of(
            LocalDate date,
            List<TodayResponseDto> todays,
            int totalPageCount
    ) {
        return new TodayListResponseDto(date, todays, totalPageCount);
    }
}
