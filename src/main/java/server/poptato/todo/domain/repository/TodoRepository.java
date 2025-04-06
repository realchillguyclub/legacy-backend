package server.poptato.todo.domain.repository;

import jakarta.persistence.Tuple;
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

    Page<Todo> findAllBacklogs(Long userId, Type type, TodayStatus status, Pageable pageable);

    Page<Todo> findBookmarkBacklogs(Long userId, Type type, TodayStatus status, Pageable pageable);

    Page<Todo> findBacklogsByCategoryId(Long userId, Long categoryId, Type type, TodayStatus status, Pageable pageable);

    void deleteAllByCategoryId(Long categoryId);

    List<Todo> findTodosDueToday(@Param("userId") Long userId, LocalDate deadline);

    void updateBacklogTodosToToday(@Param("today") LocalDate today,
                                   @Param("userIds") List<Long> userIds,
                                   @Param("basicTodayOrder") Integer basicTodayOrder);

    /**
     * 어제 한 일 중 미완료된 할 일 목록 조회.
     *
     * @param userId 사용자 ID
     * @return 미완료된 어제의 할 일 목록
     */
    List<Todo> findIncompleteYesterdays(Long userId);

    /**
     *
     * @param userId 사용자 ID
     * @param year 해당 연도
     * @param month 해당 월
     * @return 해당 연도-월 조회 일 기준 미래 날짜중 마감일이 정해진 백로그가 있는 날짜와 백로그 개수 목록
     */
    List<Tuple> findDatesWithBacklogCount(Long userId, String year, int month);
}
