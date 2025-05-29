package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.value.TodayStatus;

import java.time.LocalTime;

public record HistoryResponseDto(
        Long todoId,
        String content,
        LocalTime time,
        Boolean isCompleted
) {

    public static HistoryResponseDto from(Todo todo) {
        return new HistoryResponseDto(todo.getId(), todo.getContent(), todo.getTime(), todo.getTodayStatus().equals(TodayStatus.COMPLETED));
    }

    public static HistoryResponseDto of(Todo todo, Boolean isCompleted) {
        return new HistoryResponseDto(todo.getId(), todo.getContent(), todo.getTime(), isCompleted);
    }
}