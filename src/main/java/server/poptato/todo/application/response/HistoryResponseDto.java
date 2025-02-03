package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;

public record HistoryResponseDto(
        Long todoId,
        String content
) {

    public static HistoryResponseDto of(Todo todo) {
        return new HistoryResponseDto(todo.getId(), todo.getContent());
    }
}