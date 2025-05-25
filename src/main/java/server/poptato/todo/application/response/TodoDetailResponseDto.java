package server.poptato.todo.application.response;

import server.poptato.category.domain.entity.Category;
import server.poptato.todo.domain.entity.Todo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record TodoDetailResponseDto(
        String content,
        LocalTime time,
        LocalDate deadline,
        String categoryName,
        String emojiImageUrl,
        Boolean isBookmark,
        Boolean isRepeat,
        Boolean isRoutine,
        List<String> routineDays
) {
    public static TodoDetailResponseDto of(Todo todo, Category category, String imageUrl, List<String> routineDays) {
        return new TodoDetailResponseDto(
                todo.getContent(),
                todo.getTime(),
                todo.getDeadline(),
                category != null ? category.getName() : null,
                imageUrl,
                todo.isBookmark(),
                todo.isRepeat(),
                todo.isRoutine(),
                routineDays
        );
    }
}
