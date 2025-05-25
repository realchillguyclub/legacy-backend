package server.poptato.todo.application.response;

import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.value.TodayStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record TodayResponseDto(
        Long todoId,
        String content,
        TodayStatus todayStatus,
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
    public static TodayResponseDto of(
            Todo todo,
            String categoryName,
            String imageUrl,
            List<String> routineDays
    ) {
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
