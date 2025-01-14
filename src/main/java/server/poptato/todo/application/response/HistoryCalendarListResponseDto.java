package server.poptato.todo.application.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record HistoryCalendarListResponseDto(List<LocalDate> dates) {
}
