package server.poptato.todo.application.response;

import lombok.Getter;
import server.poptato.todo.domain.entity.Todo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
public class YesterdayResponseDto{
    Long todoId;
    Integer dDay;
    Boolean isBookmark;
    Boolean isRepeat;
    String content;

    public YesterdayResponseDto(Todo todo) {
        this.todoId = todo.getId();
        this.content = todo.getContent();
        this.isBookmark = todo.isBookmark();
        this.isRepeat = todo.isRepeat();

        if (hasDeadline(todo)) {
            this.dDay = (int) ChronoUnit.DAYS.between(LocalDate.now(), todo.getDeadline());
            return;
        }
        this.dDay = null;
    }

    private boolean hasDeadline(Todo todo) {
        return todo.getDeadline() != null;
    }
}