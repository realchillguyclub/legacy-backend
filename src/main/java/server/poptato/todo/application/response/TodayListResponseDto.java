package server.poptato.todo.application.response;

import lombok.Builder;
import lombok.Getter;
import server.poptato.todo.domain.entity.Todo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TodayListResponseDto {
    LocalDate date;
    List<TodayResponseDto> todays;
    int totalPageCount;

    @Builder
    public TodayListResponseDto(LocalDate date, List<Todo> todays, int totalPageCount) {
        this.date = date;
        this.todays = todays.stream()
                .map(TodayResponseDto::new)
                .collect(Collectors.toList());
        this.totalPageCount = totalPageCount;
    }
}
