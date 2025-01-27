package server.poptato.todo.application.response;

import org.springframework.data.domain.Page;
import server.poptato.todo.domain.entity.Todo;

import java.util.List;

public record PaginatedHistoryResponseDto(
        List<HistoryResponseDto> histories,
        int totalPageCount
) {
    public static PaginatedHistoryResponseDto of(Page<Todo> todosPage) {
        List<HistoryResponseDto> histories = todosPage.getContent().stream()
                .map(HistoryResponseDto::of)
                .toList();

        return new PaginatedHistoryResponseDto(histories, todosPage.getTotalPages());
    }
}
