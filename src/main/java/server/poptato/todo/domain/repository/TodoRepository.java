package server.poptato.todo.domain.repository;

import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoRepository {

    List<Todo> findByUserIdAndTypeAndTodayDateAndTodayStatusOrderByTodayOrderDesc(Long userId, Type type, LocalDate todayDate, TodayStatus todayStatus);

    List<Todo> findIncompleteTodosWithCategory(Long userId, Type type, LocalDate todayDate, TodayStatus todayStatus);

    List<Todo> findCompletedTodayByUserIdOrderByCompletedDateTimeAsc(Long userId, LocalDate todayDate);

    List<Todo> findCompletedTodayByUserIdOrderByCompletedDateTimeAscWithCategory(Long userId, LocalDate todayDate);

    Optional<Todo> findById(Long todoId);

    boolean existsById(Long id);

    void delete(Todo todo);

    Todo save(Todo todo);

    Page<Todo> findByUserIdAndTypeAndTodayStatus(Long userId, Type type, TodayStatus todayStatus, Pageable pageable);

    Integer findMaxTodayOrderByUserIdOrZero(Long userId);

    Integer findMinTodayOrderByUserIdOrZero(Long userId);

    Integer findMaxBacklogOrderByUserIdOrZero(Long userId);

    default Page<Todo> findDeadlineBacklogs(Long userId, LocalDate localDate, Pageable pageable) {
        return findDeadlineBacklogsByUserIdAndLocalDate(userId, localDate, pageable);
    }

    Page<Todo> findDeadlineBacklogsByUserIdAndLocalDate(Long userId, LocalDate localDate, Pageable pageable);

    default List<Todo> findIncompleteTodays(Long userId, Type type, LocalDate todayDate, TodayStatus todayStatus) {
        return findByUserIdAndTypeAndTodayDateAndTodayStatusOrderByTodayOrderDesc(
                userId, type, todayDate, todayStatus);
    }

    default List<Todo> findIncompleteTodaysWithCategory(Long userId, Type type, LocalDate todayDate, TodayStatus todayStatus) {
        return findIncompleteTodosWithCategory(
                userId, type, todayDate, todayStatus);
    }

    default List<Todo> findCompletedTodays(Long userId, LocalDate todayDate) {
        return findCompletedTodayByUserIdOrderByCompletedDateTimeAsc(
                userId, todayDate);
    }

    default List<Todo> findCompletedTodaysWithCategory(Long userId, LocalDate todayDate) {
        return findCompletedTodayByUserIdOrderByCompletedDateTimeAscWithCategory(
                userId, todayDate);
    }

    default Page<Todo> findHistories(Long userId,LocalDate localDate, Pageable pageable) {
        return findTodosByUserIdAndCompletedDateTime(userId, localDate, pageable);
    }

    Page<Todo> findTodosByUserIdAndCompletedDateTime(Long userId, LocalDate localDate, Pageable pageable);

    List<Todo> findByType(Type type);

    Page<Todo> findAllBacklogs(Long userId, Type type, TodayStatus status, Pageable pageable);

    Page<Todo> findBookmarkBacklogs(Long userId, Type type, TodayStatus status, Pageable pageable);

    Page<Todo> findBacklogsByCategoryId(Long userId, Long categoryId, Type type, TodayStatus status, Pageable pageable);

    void deleteAllByCategoryId(Long categoryId);

    @Query("""
    SELECT t FROM Todo t
    WHERE t.userId = :userId
      AND t.deadline = :deadline
      AND t.todayStatus = :todayStatus
    """)
    List<Todo> findTodosDueToday(@Param("userId") Long userId,
                                 @Param("deadline") LocalDate deadline,
                                 @Param("todayStatus") TodayStatus todayStatus);

    void updateBacklogTodosToToday(@Param("today") LocalDate today,
                                   @Param("userIds") List<Long> userIds,
                                   @Param("basicTodayOrder") Integer basicTodayOrder);

    List<Todo> findIncompleteYesterdays(Long userId);

    List<Tuple> findDatesWithBacklogCount(Long userId, String year, int month);

    boolean existsByUserIdAndTypeAndTodayStatus(Long userId, Type type, TodayStatus todayStatus);
}
