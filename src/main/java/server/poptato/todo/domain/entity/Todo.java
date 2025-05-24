package server.poptato.todo.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.poptato.category.domain.entity.Category;
import server.poptato.global.dao.BaseEntity;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "todo")
public class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id")
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Lob
    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    private LocalTime time;

    private LocalDate deadline;

    @Column(name = "is_bookmark", nullable = false)
    private boolean isBookmark;

    @Column(name = "is_repeat", nullable = false)
    private boolean isRepeat;

    @Column(name = "today_date")
    private LocalDate todayDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "today_status")
    private TodayStatus todayStatus;

    @Column(name = "today_order")
    private Integer todayOrder;

    @Column(name = "backlog_order")
    private Integer backlogOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @Builder
    public Todo(Long userId, Long categoryId, Type type, String content, LocalTime time, LocalDate deadline,
                boolean isBookmark, boolean isRepeat, LocalDate todayDate, TodayStatus todayStatus,
                Integer todayOrder, Integer backlogOrder) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.type = type;
        this.content = content;
        this.time = time;
        this.deadline = deadline;
        this.isBookmark = isBookmark;
        this.isRepeat = isRepeat;
        this.todayDate = todayDate;
        this.todayStatus = todayStatus;
        this.todayOrder = todayOrder;
        this.backlogOrder = backlogOrder;
    }

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
                .categoryId(categoryId)
                .content(content)
                .backlogOrder(backlogOrder)
                .isBookmark(false)
                .type(Type.BACKLOG)
                .build();
    }

    public static Todo createYesterdayBacklog(Long userId, String content, Integer backlogOrder) {
        return Todo.builder()
                .userId(userId)
                .content(content)
                .backlogOrder(backlogOrder)
                .todayDate(LocalDate.now().minusDays(1))
                .isBookmark(false)
                .type(Type.YESTERDAY)
                .todayStatus(TodayStatus.INCOMPLETE)
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

    public void updateTime(LocalTime time) {
        this.time = time;
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

    public void updateCategory(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void updateIsRepeat() {
        this.isRepeat = !this.isRepeat;
    }

    public void updateTodayStatus(TodayStatus todayStatus) {
        this.todayStatus = todayStatus;
    }

    public void updateType(Type type) {
        this.type = type;
    }

    public void updateTodayOrder(Integer order) {
        this.todayOrder = order;
    }

    public void updateBacklogOrder(int order) {
        this.backlogOrder = order;
    }
}
