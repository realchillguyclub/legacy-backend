package server.poptato.todo.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.poptato.global.dao.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "completed_date_time")
public class CompletedDateTime extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "todo_id", nullable = false)
    private Long todoId;

    @Builder
    public CompletedDateTime(Long todoId) {
        this.todoId = todoId;
    }
}
