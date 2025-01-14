package server.poptato.todo.api.request;

import lombok.Builder;

@Builder
public record TodoCategoryUpdateRequestDto(Long categoryId) {
}
