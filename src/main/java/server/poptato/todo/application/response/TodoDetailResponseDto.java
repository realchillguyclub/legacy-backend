package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;
import server.poptato.category.domain.entity.Category;
import server.poptato.emoji.domain.entity.Emoji;

import java.time.LocalDate;

public record TodoDetailResponseDto(
        String content,
        LocalDate deadline,
        String categoryName,
        String emojiImageUrl,
        Boolean isBookmark,
        Boolean isRepeat
) {
    public static TodoDetailResponseDto of(Todo todo, Category category, Emoji emoji) {
        return new TodoDetailResponseDto(
                todo.getContent(),
                todo.getDeadline(),
                category != null ? category.getName() : null,
                emoji != null ? emoji.getImageUrl() : null,
                todo.isBookmark(),
                todo.isRepeat()
        );
    }
}
