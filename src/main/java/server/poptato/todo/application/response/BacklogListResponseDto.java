package server.poptato.todo.application.response;

import lombok.Builder;
import lombok.Getter;
import server.poptato.todo.domain.entity.Todo;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BacklogListResponseDto {
    long totalCount;
    String categoryName;
    List<BacklogResponseDto> backlogs;
    int totalPageCount;

    @Builder
    public BacklogListResponseDto(String categoryName, long totalCount, List<Todo> backlogs, int totalPageCount) {
        this.categoryName = categoryName;
        this.totalCount = totalCount;
        this.backlogs = backlogs.stream()
                .map(BacklogResponseDto::new)
                .collect(Collectors.toList());
        this.totalPageCount = totalPageCount;
    }
}
