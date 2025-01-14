package server.poptato.todo.application.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import server.poptato.todo.domain.entity.Todo;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PaginatedYesterdayResponseDto {
    List<YesterdayResponseDto> yesterdays;
    int totalPageCount;

    @Builder
    public PaginatedYesterdayResponseDto(Page<Todo> yesterdaysPage) {
        this.yesterdays = yesterdaysPage.getContent().stream()
                .map(YesterdayResponseDto::new)
                .collect(Collectors.toList());
        this.totalPageCount = yesterdaysPage.getTotalPages();
    }
}
