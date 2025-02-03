package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record YesterdayResponseDto(
        Long todoId,
        Integer dDay,
        Boolean isBookmark,
        Boolean isRepeat,
        String content
) {
    public static YesterdayResponseDto of(Todo todo) {
        Integer dDay = null;
        if (todo.getDeadline() != null) {
            dDay = (int) ChronoUnit.DAYS.between(LocalDate.now(), todo.getDeadline());
        }

        return new YesterdayResponseDto(
                todo.getId(),
                dDay,
                todo.isBookmark(),
                todo.isRepeat(),
                todo.getContent()
        );
    }
}
