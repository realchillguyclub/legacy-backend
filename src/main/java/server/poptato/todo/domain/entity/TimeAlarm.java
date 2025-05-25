package server.poptato.todo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "time_alarm")
public class TimeAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "todo_id", nullable = false)
    private Long  todoId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "notified", nullable = false)
    private boolean notified;

    @Builder
    public TimeAlarm(Long todoId, Long userId) {
        this.todoId = todoId;
        this.userId = userId;
        this.notified = false;
    }

    public void updateNotified(boolean notified) {
        this.notified = notified;
    }
}
