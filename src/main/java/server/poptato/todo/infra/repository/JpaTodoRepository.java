package server.poptato.todo.infra.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;

import java.time.LocalDate;
import java.util.List;

public interface JpaTodoRepository extends TodoRepository, JpaRepository<Todo, Long> {
    @Query("SELECT COALESCE(MAX(t.backlogOrder), 0) FROM Todo t WHERE t.userId = :userId AND t.backlogOrder IS NOT NULL")
    Integer findMaxBacklogOrderByUserIdOrZero(Long userId);

    @Query("SELECT COALESCE(MAX(t.todayOrder), 0) FROM Todo t WHERE t.userId = :userId AND t.todayOrder IS NOT NULL")
    Integer findMaxTodayOrderByUserIdOrZero(Long userId);

    @Query("SELECT COALESCE(MIN(t.backlogOrder), 0) FROM Todo t WHERE t.userId = :userId AND t.backlogOrder IS NOT NULL")
    Integer findMinBacklogOrderByUserIdOrZero(@Param("userId") Long userId);

    @Query("SELECT COALESCE(MIN(t.todayOrder), 0) FROM Todo t WHERE t.userId = :userId AND t.todayOrder IS NOT NULL")
    Integer findMinTodayOrderByUserIdOrZero(Long userId);

    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND (t.type IN :types " +
            "AND (t.todayStatus != :status OR t.todayStatus IS NULL)) " +
            "ORDER BY t.backlogOrder DESC")
    Page<Todo> findAllBacklogs(
            @Param("userId") Long userId,
            @Param("types") List<Type> types,
            @Param("status") TodayStatus status,
            Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.isBookmark = true " +
            "AND t.type IN :types AND (t.todayStatus != :status " +
            "OR t.todayStatus IS NULL) ORDER BY t.backlogOrder DESC")
    Page<Todo> findBookmarkBacklogs(
            @Param("userId") Long userId,
            @Param("types") List<Type> types,
            @Param("status") TodayStatus status,
            Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.categoryId = :categoryId AND " +
            "(t.type IN :types AND (t.todayStatus != :status OR t.todayStatus IS NULL)) " +
            "ORDER BY t.backlogOrder DESC")
    Page<Todo> findBacklogsByCategoryId(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("types") List<Type> types,
            @Param("status") TodayStatus status,
            Pageable pageable);

    @Query("""
            SELECT t 
            FROM Todo t
            JOIN CompletedDateTime c ON t.id = c.todoId
            WHERE t.userId = :userId 
              AND t.type = 'TODAY'
              AND t.todayStatus = 'COMPLETED'
              AND FUNCTION('DATE', c.dateTime) = :todayDate
            ORDER BY c.dateTime ASC
            """)
    List<Todo> findCompletedTodayByUserIdOrderByCompletedDateTimeAsc(
            @Param("userId") Long userId,
            @Param("todayDate") LocalDate todayDate
    );

    @Query("SELECT t FROM Todo t " +
            "WHERE t.id IN (" +
            "    SELECT c.todoId FROM CompletedDateTime c " +
            "    WHERE DATE(c.dateTime) = :localDate" +
            ") AND t.userId = :userId " +
            "ORDER BY (" +
            "    SELECT c.dateTime FROM CompletedDateTime c " +
            "    WHERE c.todoId = t.id AND DATE(c.dateTime) = :localDate" +
            ") ASC")
    Page<Todo> findTodosByUserIdAndCompletedDateTime(@Param("userId") Long userId,
                                                     @Param("localDate") LocalDate localDate, Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.deadline = :deadline")
    List<Todo> findTodosDueToday(@Param("userId") Long userId, @Param("deadline") LocalDate deadline);

    @Modifying(clearAutomatically=true)
    @Query("""
    UPDATE Todo t
    SET t.type = 'TODAY',
        t.todayOrder = :basicTodayOrder,
        t.todayStatus = 'INCOMPLETE',
        t.todayDate = CURRENT_DATE,
        t.backlogOrder = NULL
    WHERE t.type = 'BACKLOG'
    AND t.deadline = CURRENT_DATE
    AND t.userId IN :userIds
""")
    void updateBacklogTodosToToday(@Param("userIds") List<Long> userIds,
                                  @Param("basicTodayOrder") Integer basicTodayOrder);
}
