package server.poptato.todo.application.response;

import server.poptato.category.domain.entity.Category;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.global.util.FileUtil;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.user.domain.value.MobileType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public record BacklogResponseDto(
        Long todoId,
        String content,
        Boolean isBookmark,
        Boolean isRepeat,
        Boolean isRoutine,
        Integer dDay,
        LocalTime time,
        LocalDate deadline,
        List<String> routineDays,
        String categoryName,
        String imageUrl
) {

    public static BacklogResponseDto of(Todo todo, MobileType mobileType, List<String> routineDays) {
        LocalDate today = LocalDate.now();
        Integer dDay = (todo.getDeadline() != null)
                ? (int) ChronoUnit.DAYS.between(today, todo.getDeadline())
                : null;

        String categoryName = Optional.ofNullable(todo.getCategory())
                .map(Category::getName)
                .orElse(null);

        String imageUrl = Optional.ofNullable(todo.getCategory())
                .map(Category::getEmoji)
                .map(Emoji::getImageUrl)
                .map(url -> FileUtil.changeFileExtension(url, mobileType.getImageUrlExtension()))
                .orElse(null);

        return new BacklogResponseDto(
                todo.getId(),
                todo.getContent(),
                todo.isBookmark(),
                todo.isRepeat(),
                todo.isRoutine(),
                dDay,
                todo.getTime(),
                todo.getDeadline(),
                routineDays,
                categoryName,
                imageUrl
        );
    }
}
