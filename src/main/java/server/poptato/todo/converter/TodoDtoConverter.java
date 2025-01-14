package server.poptato.todo.converter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import server.poptato.category.domain.entity.Category;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.todo.application.response.*;
import server.poptato.todo.domain.entity.Todo;

import java.time.LocalDate;
import java.util.List;

@Component
public class TodoDtoConverter {
    public static TodayListResponseDto toTodayListDto(LocalDate todayDate, List<Todo> todaySubList, int totalPageCount) {
        return TodayListResponseDto.builder()
                .date(todayDate)
                .todays(todaySubList)
                .totalPageCount(totalPageCount)
                .build();
    }

    public static BacklogListResponseDto toBacklogListDto(String categoryName, Page<Todo> backlogs) {
        return BacklogListResponseDto.builder()
                .totalCount(backlogs.getTotalElements())
                .categoryName(categoryName)
                .backlogs(backlogs.getContent())
                .totalPageCount(backlogs.getTotalPages())
                .build();
    }

    public static BacklogCreateResponseDto toBacklogCreateDto(Todo todo) {
        return BacklogCreateResponseDto.builder()
                .todoId(todo.getId())
                .build();
    }

    public static TodoDetailResponseDto toTodoDetailInfoDto(Todo todo, Category category, Emoji emoji) {
        return TodoDetailResponseDto.builder()
                .content(todo.getContent())
                .isBookmark(todo.isBookmark())
                .isRepeat(todo.isRepeat())
                .categoryName(category != null ? category.getName() : null)
                .emojiImageUrl(emoji != null ? emoji.getImageUrl() : null)
                .deadline(todo.getDeadline())
                .build();
    }

    public static PaginatedHistoryResponseDto toHistoryListDto(Page<Todo> todosPage) {
        return PaginatedHistoryResponseDto
                .builder()
                .todosPage(todosPage)
                .build();
    }

    public static PaginatedYesterdayResponseDto toYesterdayListDto(Page<Todo> yesterdaysPage) {
        return PaginatedYesterdayResponseDto
                .builder()
                .yesterdaysPage(yesterdaysPage)
                .build();
    }

    private TodoDtoConverter() {
    }
}
