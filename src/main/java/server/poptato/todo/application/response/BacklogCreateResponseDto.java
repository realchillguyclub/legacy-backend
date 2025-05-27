package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;

public record BacklogCreateResponseDto(
        Long todoId
){

    public static BacklogCreateResponseDto from(Todo todo) {
        return new BacklogCreateResponseDto(todo.getId());
    }
}
