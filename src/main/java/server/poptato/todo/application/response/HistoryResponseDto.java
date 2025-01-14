package server.poptato.todo.application.response;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record HistoryResponseDto(Long todoId, String content) {
}