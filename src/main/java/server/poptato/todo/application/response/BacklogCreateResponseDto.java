package server.poptato.todo.application.response;

import lombok.Builder;

@Builder
public record BacklogCreateResponseDto(Long todoId){
}