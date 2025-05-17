package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public record BacklogResponseDto(
        Long todoId,
        String content,
        Boolean isBookmark,
        Boolean isRepeat,
        Integer dDay,
        LocalTime time,
        LocalDate deadline,
        String categoryName,
        String imageUrl
) {

    public static BacklogResponseDto of(Todo todo, String categoryName, String imageUrl) {
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
                todo.getTime(),
                todo.getDeadline(),
                categoryName,
                imageUrl
        );
    }
}
