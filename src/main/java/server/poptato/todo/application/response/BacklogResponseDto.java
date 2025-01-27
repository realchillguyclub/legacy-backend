package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record BacklogResponseDto(
        Long id,
        String content,
        Boolean isBookmark,
        Boolean isRepeat,
        Integer dDay,
        LocalDate deadline
) {

    public static BacklogResponseDto from(Todo todo) {
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
                todo.getDeadline()
        );
    }
}
