package server.poptato.todo.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoRepository {

    List<Todo> findByUserIdAndTypeAndTodayDateAndTodayStatusOrderByTodayOrderDesc(Long userId, Type type, LocalDate todayDate, TodayStatus todayStatus);

    List<Todo> findCompletedTodayByUserIdOrderByCompletedDateTimeAsc(Long userId, LocalDate todayDate);

    Optional<Todo> findById(Long todoId);

    void delete(Todo todo);

    Todo save(Todo todo);

    Page<Todo> findByUserIdAndTypeAndTodayStatus(Long userId, Type type, TodayStatus todayStatus, Pageable pageable);

    Integer findMaxTodayOrderByUserIdOrZero(Long userId);

    Integer findMinTodayOrderByUserIdOrZero(Long userId);

    Integer findMaxBacklogOrderByUserIdOrZero(Long userId);

    /* 25.02.20 : 미사용으로 인한 주석 처리
    Integer findMinBacklogOrderByUserIdOrZero(Long userId);
     */
    default List<Todo> findIncompleteTodays(Long userId, Type type, LocalDate todayDate, TodayStatus todayStatus) {
        return findByUserIdAndTypeAndTodayDateAndTodayStatusOrderByTodayOrderDesc(
                userId, type, todayDate, todayStatus);
    }

    default List<Todo> findCompletedTodays(Long userId, LocalDate todayDate) {
        return findCompletedTodayByUserIdOrderByCompletedDateTimeAsc(
                userId, todayDate);
    }

    default Page<Todo> findHistories(Long userId,LocalDate localDate, Pageable pageable) {
        return findTodosByUserIdAndCompletedDateTime(userId, localDate, pageable);
    }
    Page<Todo> findTodosByUserIdAndCompletedDateTime(Long userId, LocalDate localDate, Pageable pageable);

    List<Todo> findByType(Type type);

    Page<Todo> findAllBacklogs(Long userId, List<Type> types, TodayStatus status, Pageable pageable);

    Page<Todo> findBookmarkBacklogs(Long userId, List<Type> types, TodayStatus status, Pageable pageable);

    Page<Todo> findBacklogsByCategoryId(Long userId, Long categoryId, List<Type> types, TodayStatus status, Pageable pageable);

    void deleteAllByCategoryId(Long categoryId);

    List<Todo> findTodosDueToday(@Param("userId") Long userId, LocalDate deadline);

    void updateBacklogTodosToToday(@Param("today") LocalDate today,
                                   @Param("userIds") List<Long> userIds,
                                   @Param("basicTodayOrder") Integer basicTodayOrder);
}
