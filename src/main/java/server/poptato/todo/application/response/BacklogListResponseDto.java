package server.poptato.todo.application.response;

import org.springframework.data.domain.Page;
import server.poptato.todo.domain.entity.Todo;

import java.util.List;
import java.util.stream.Collectors;

public record BacklogListResponseDto(
        long totalCount,
        String categoryName,
        List<BacklogResponseDto> backlogs,
        int totalPageCount
){

    public static BacklogListResponseDto of (String categoryName, Page<Todo> backlogs) {
        return new BacklogListResponseDto(
                backlogs.getTotalElements(),
                categoryName,
                backlogs.getContent().stream()
                        .map(BacklogResponseDto::from)
                        .collect(Collectors.toList()),
                backlogs.getTotalPages()
        );
    }
}