package server.poptato.todo.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
    private Boolean notified = false;

    @Builder
    public TimeAlarm(Long todoId, Long userId) {
        this.todoId = todoId;
        this.userId = userId;
        this.notified = false;
    }

    public void updateNotified(Boolean notified) {
        this.notified = notified;
    }

}
