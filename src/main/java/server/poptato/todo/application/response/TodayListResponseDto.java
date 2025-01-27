package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record TodayListResponseDto(
        LocalDate date,
        List<TodayResponseDto> todays,
        int totalPageCount
) {
    public static TodayListResponseDto of(LocalDate date, List<Todo> todos, int totalPageCount) {
        return new TodayListResponseDto(
                date,
                todos.stream()
                        .map(TodayResponseDto::of)
                        .collect(Collectors.toList()),
                totalPageCount
        );
    }
}
