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

    /**
     * 어제 한 일 중 미완료된 할 일 목록 조회.
     *
     * @param userId 사용자 ID
     * @return 미완료된 어제의 할 일 목록
     */
    List<Todo> findIncompleteYesterdays(Long userId);

    /**
     * 해당 연도와 월 기준으로, 미래 날짜 중 마감일이 설정된 백로그가 존재하는 날짜와 해당 날짜별 백로그 개수를 조회한다.
     *
     * @param userId 사용자 ID
     * @param year 조회할 연도 (예: "2025")
     * @param month 조회할 월 (1~12)
     * @return Tuple 리스트 (날짜, 백로그 개수)
     */
    List<Tuple> findDatesWithBacklogCount(Long userId, String year, int month);

    /**
     * 주어진 사용자 ID에 대해, 타입이 TODAY이고 상태가 INCOMPLETE인 할 일이 존재하는지 확인한다.
     *
     * @param userId 사용자 ID
     * @param type 할 일 타입 (예: TODAY)
     * @param todayStatus 오늘 상태 (예: INCOMPLETE)
     * @return 조건에 맞는 할 일이 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByUserIdAndTypeAndTodayStatus(Long userId, Type type, TodayStatus todayStatus);
}
