package server.poptato.todo.application.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import server.poptato.todo.domain.entity.Todo;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PaginatedHistoryResponseDto {
    List<HistoryResponseDto> histories;
    int totalPageCount;

    @Builder
    public PaginatedHistoryResponseDto(Page<Todo> todosPage) {
        this.histories = todosPage.getContent().stream()
                .map(todo -> new HistoryResponseDto(
                        todo.getId(),
                        todo.getContent()
                ))
                .collect(Collectors.toList());

        this.totalPageCount = todosPage.getTotalPages();
    }

}