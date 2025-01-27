package server.poptato.todo.application.response;

import org.springframework.data.domain.Page;
import server.poptato.todo.domain.entity.Todo;

import java.util.List;

public record PaginatedYesterdayResponseDto(
        List<YesterdayResponseDto> yesterdays,
        int totalPageCount
) {
    public static PaginatedYesterdayResponseDto of(Page<Todo> yesterdaysPage) {
        List<YesterdayResponseDto> yesterdays = yesterdaysPage.getContent().stream()
                .map(YesterdayResponseDto::of)
                .toList();
        return new PaginatedYesterdayResponseDto(yesterdays, yesterdaysPage.getTotalPages());
    }
}
