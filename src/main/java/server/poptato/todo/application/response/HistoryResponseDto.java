package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.value.TodayStatus;

public record HistoryResponseDto(
        Long todoId,
        String content,
        Boolean isCompleted
) {

    public static HistoryResponseDto of(Todo todo) {
        return new HistoryResponseDto(todo.getId(), todo.getContent(), todo.getTodayStatus().equals(TodayStatus.COMPLETED));
    }

    public static HistoryResponseDto of(Todo todo, Boolean isCompleted) {
        return new HistoryResponseDto(todo.getId(), todo.getContent(), isCompleted);
    }
}