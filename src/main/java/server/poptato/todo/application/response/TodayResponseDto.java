package server.poptato.todo.application.response;

import lombok.Getter;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.value.TodayStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
public class TodayResponseDto {
    Long todoId;
    String content;
    TodayStatus todayStatus;
    Boolean isBookmark;
    Boolean isRepeat;
    Integer dDay;
    LocalDate deadline;

    public TodayResponseDto(Todo todo) {
        this.todoId = todo.getId();
        this.content = todo.getContent();
        this.todayStatus = todo.getTodayStatus();
        this.isBookmark = todo.isBookmark();
        this.isRepeat = todo.isRepeat();
        this.deadline = todo.getDeadline();

        if (hasDeadline(todo)) {
            this.dDay = (int) ChronoUnit.DAYS.between(todo.getTodayDate(), todo.getDeadline());
            return;
        }
        this.dDay = null;
    }

    private boolean hasDeadline(Todo todo) {
        return todo.getDeadline() != null && todo.getTodayDate() != null;
    }
}
