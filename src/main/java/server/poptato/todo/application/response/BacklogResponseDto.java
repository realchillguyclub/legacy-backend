package server.poptato.todo.application.response;

import lombok.Getter;
import server.poptato.todo.domain.entity.Todo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
public class BacklogResponseDto {
    Long todoId;
    String content;
    Boolean isBookmark;
    Boolean isRepeat;
    Integer dDay;
    LocalDate deadline;

    public BacklogResponseDto(Todo todo) {
        this.todoId = todo.getId();
        this.content = todo.getContent();
        this.isBookmark = todo.isBookmark();
        this.isRepeat = todo.isRepeat();
        this.deadline = todo.getDeadline();
        LocalDate todayDate = LocalDate.now();
        if (this.deadline==null)
            this.dDay = null;
        else this.dDay = (int) ChronoUnit.DAYS.between(todayDate, todo.getDeadline());
    }
}
