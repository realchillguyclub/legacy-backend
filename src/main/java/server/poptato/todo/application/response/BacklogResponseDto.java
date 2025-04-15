package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record BacklogResponseDto(
        Long todoId,
        String content,
        Boolean isBookmark,
        Boolean isRepeat,
        Integer dDay,
        LocalDate deadline,
        String detailCategoryName,
        String imageUrl
) {

    public static BacklogResponseDto of(Todo todo, String detailCategoryName, String imageUrl) {
        LocalDate today = LocalDate.now();
        Integer dDay = null;

        if (todo.getDeadline() != null) {
            dDay = (int) ChronoUnit.DAYS.between(today, todo.getDeadline());
        }

        return new BacklogResponseDto(
                todo.getId(),
                todo.getContent(),
                todo.isBookmark(),
                todo.isRepeat(),
                dDay,
                todo.getDeadline(),
                detailCategoryName,
                imageUrl
        );
    }
}
