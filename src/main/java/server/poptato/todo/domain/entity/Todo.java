package server.poptato.todo.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Todo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long userId;
    @Nullable
    private Long  categoryId;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;
    @NotNull
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    @Nullable
    private LocalDate deadline;
    @NotNull
    private boolean isBookmark;
    @NotNull
    private boolean isRepeat;
    @Nullable
    private LocalDate todayDate;
    @Enumerated(EnumType.STRING)
    private TodayStatus todayStatus;
    @Nullable
    private Integer todayOrder;
    @Nullable
    private Integer backlogOrder;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate;
    @LastModifiedDate
    private LocalDateTime modifyDate;

    public static Todo createBacklog(Long userId, String content, Integer backlogOrder) {
        return Todo.builder()
                .userId(userId)
                .content(content)
                .backlogOrder(backlogOrder)
                .isBookmark(false)
                .type(Type.BACKLOG)
                .build();
    }

    public static Todo createBookmarkBacklog(Long userId, String content, int backlogOrder) {
        return Todo.builder()
                .userId(userId)
                .content(content)
                .backlogOrder(backlogOrder)
                .isBookmark(true)
                .type(Type.BACKLOG)
                .build();
    }

    public static Todo createCategoryBacklog(Long userId, Long categoryId, String content, int backlogOrder) {
        return Todo.builder()
                .userId(userId)
                .content(content)
                .backlogOrder(backlogOrder)
                .isBookmark(false)
                .type(Type.BACKLOG)
                .categoryId(categoryId)
                .build();
    }

    public void toggleBookmark() {
        this.isBookmark = !this.isBookmark;
    }

    public void changeToToday(Integer maxTodayOrder) {
        this.type = Type.TODAY;
        this.backlogOrder = null;
        this.todayOrder = maxTodayOrder + 1;
        this.todayStatus = TodayStatus.INCOMPLETE;
        this.todayDate = LocalDate.now();
    }

    public void changeToBacklog(Integer maxBacklogOrder) {
        this.type = Type.BACKLOG;
        this.backlogOrder = maxBacklogOrder + 1;
        this.todayOrder = null;
        this.todayStatus = null;
        this.todayDate = null;
    }

    public void setTodayOrder(Integer order) {
        this.todayOrder = order;
    }

    public void setBacklogOrder(int order) {
        this.backlogOrder = order;
    }

    public void updateDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateTodayToInComplete(int minTodayOrder) {
        this.todayStatus = TodayStatus.INCOMPLETE;
        this.todayOrder = --minTodayOrder;
    }

    public void updateTodayToCompleted() {
        this.todayStatus = TodayStatus.COMPLETED;
        this.todayOrder = null;
    }

    public void updateYesterdayToCompleted() {
        this.todayStatus = TodayStatus.COMPLETED;
        this.backlogOrder = null;
    }

    public void updateYesterdayToInComplete(int maxBacklogOrder) {
        this.todayStatus = TodayStatus.COMPLETED;
        this.backlogOrder = ++maxBacklogOrder;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setTodayStatus(TodayStatus todayStatus) {
        this.todayStatus = todayStatus;
    }

    public void updateCategory(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void updateIsRepeat() {
        this.isRepeat=!this.isRepeat;
    }
}
