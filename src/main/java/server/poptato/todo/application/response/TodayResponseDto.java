package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.value.TodayStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record TodayResponseDto(
        Long todoId,
        String content,
        TodayStatus todayStatus,
        Boolean isBookmark,
        Boolean isRepeat,
        Integer dDay,
        LocalDate deadline
) {
    public static TodayResponseDto of(Todo todo) {
        Integer dDay = null;
        if (todo.getDeadline() != null && todo.getTodayDate() != null) {
            dDay = (int) ChronoUnit.DAYS.between(todo.getTodayDate(), todo.getDeadline());
        }

        return new TodayResponseDto(
                todo.getId(),
                todo.getContent(),
                todo.getTodayStatus(),
                todo.isBookmark(),
                todo.isRepeat(),
                dDay,
                todo.getDeadline()
        );
    }
}
