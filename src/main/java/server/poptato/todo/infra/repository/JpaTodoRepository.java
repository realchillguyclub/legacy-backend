package server.poptato.todo.infra.repository;

import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;

import java.time.LocalDate;
import java.util.List;

public interface JpaTodoRepository extends JpaRepository<Todo, Long> {

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
    List<Todo> findCompletedTodays(
            @Param("userId") Long userId,
            @Param("todayDate") LocalDate todayDate
    );

    @EntityGraph(attributePaths = {"category", "category.emoji"})
    @Query("""
        SELECT t
        FROM Todo t
        WHERE t.userId = :userId
          AND t.type = :type
          AND t.todayDate = :todayDate
          AND t.todayStatus = :todayStatus
        ORDER BY t.todayOrder DESC
    """)
    List<Todo> findIncompleteTodays(
            @Param("userId") Long userId,
            @Param("type") Type type,
            @Param("todayDate") LocalDate todayDate,
            @Param("todayStatus") TodayStatus todayStatus
    );

    @Query("""
    SELECT t
    FROM Todo t
    WHERE t.userId = :userId
      AND t.type = :type
      AND t.todayDate = :todayDate
      AND t.todayStatus = :todayStatus
    ORDER BY t.todayOrder DESC
    """)
    List<Todo> findIncompleteTodaysWithCategory(@Param("userId") Long userId,
                                     @Param("type") Type type,
                                     @Param("todayDate") LocalDate todayDate,
                                     @Param("todayStatus") TodayStatus todayStatus);

    @EntityGraph(attributePaths = {"category", "category.emoji"})
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
    List<Todo> findCompletedTodaysWithCategory(
            @Param("userId") Long userId,
            @Param("todayDate") LocalDate todayDate
    );

    Page<Todo> findByUserIdAndTypeAndTodayStatus(Long userId, Type type, TodayStatus todayStatus, Pageable pageable);

    @Query("""
        SELECT COALESCE(MAX(t.todayOrder), 0)
        FROM Todo t
        WHERE t.userId = :userId
          AND t.todayOrder IS NOT NULL
    """)
    Integer findMaxTodayOrderByUserIdOrZero(Long userId);

    @Query("""
        SELECT COALESCE(MIN(t.todayOrder), 0)
        FROM Todo t
        WHERE t.userId = :userId
          AND t.todayOrder IS NOT NULL
    """)
    Integer findMinTodayOrderByUserIdOrZero(Long userId);

    @Query("""
        SELECT COALESCE(MAX(t.backlogOrder), 0)
        FROM Todo t
        WHERE t.userId = :userId
          AND t.backlogOrder IS NOT NULL
    """)
    Integer findMaxBacklogOrderByUserIdOrZero(Long userId);

    @EntityGraph(attributePaths = {"category", "category.emoji"})
    @Query("""
        SELECT t
        FROM Todo t
        WHERE t.userId = :userId
          AND t.type = :type
          AND (t.todayStatus != :status OR t.todayStatus IS NULL)
        ORDER BY t.backlogOrder DESC
    """)
    Page<Todo> findAllBacklogs(
            @Param("userId") Long userId,
            @Param("type") Type type,
            @Param("status") TodayStatus status,
            Pageable pageable
    );

    @Query("""
    SELECT t
    FROM Todo t
    WHERE t.userId = :userId
      AND t.deadline = :localDate
      AND t.type IN ('BACKLOG', 'YESTERDAY')
    """)
    Page<Todo> findDeadlineBacklogs(
            @Param("userId") Long userId,
            @Param("localDate") LocalDate localDate,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"category", "category.emoji"})
    @Query("""
        SELECT t
        FROM Todo t
        WHERE t.userId = :userId
          AND t.isBookmark = true
          AND t.type = :type
          AND (t.todayStatus != :status OR t.todayStatus IS NULL)
        ORDER BY t.backlogOrder DESC
    """)
    Page<Todo> findBookmarkBacklogs(
            @Param("userId") Long userId,
            @Param("type") Type type,
            @Param("status") TodayStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"category", "category.emoji"})
    @Query("""
        SELECT t
        FROM Todo t
        WHERE t.userId = :userId
          AND t.categoryId = :categoryId
          AND t.type = :type
          AND (t.todayStatus != :status OR t.todayStatus IS NULL)
        ORDER BY t.backlogOrder DESC
    """)
    Page<Todo> findBacklogsByCategoryId(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("type") Type type,
            @Param("status") TodayStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT t
        FROM Todo t
        WHERE t.id IN (
            SELECT c.todoId
            FROM CompletedDateTime c
            WHERE DATE(c.dateTime) = :localDate
        )
          AND t.userId = :userId
        ORDER BY (
            SELECT c.dateTime
            FROM CompletedDateTime c
            WHERE c.todoId = t.id
              AND DATE(c.dateTime) = :localDate
        ) ASC
    """)
    Page<Todo> findHistories(
            @Param("userId") Long userId,
            @Param("localDate") LocalDate localDate,
            Pageable pageable
    );

    List<Todo> findByType(Type type);

    void deleteAllByCategoryId(Long categoryId);

    @Query("""
    SELECT t FROM Todo t
    WHERE t.userId = :userId
      AND t.todayStatus = :todayStatus
      AND t.type = 'TODAY'
    """)
    List<Todo> findIncompleteTodayTodos(@Param("userId") Long userId,
                                 @Param("todayStatus") TodayStatus todayStatus
    );

    @Query("""
    SELECT t FROM Todo t
    WHERE t.type = 'BACKLOG'
        AND t.userId = :userId
        AND t.deadline = :deadline
    """)
    List<Todo> findTodosByDeadLine(@Param("userId") Long userId,
                                 @Param("deadline") LocalDate deadline);

    @Query(value = """
    SELECT t.* FROM todo t
    JOIN routine r ON t.id = r.todo_id
    WHERE t.type = 'BACKLOG'
      AND r.day = :todayDay
      AND t.user_id = :userId
    """, nativeQuery = true)
    List<Todo> findRoutineTodosByDay(@Param("userId") Long userId,
                                     @Param("todayDay") String todayDay);

    @Query("""
        SELECT t
        FROM Todo t
        WHERE t.userId = :userId
          AND t.type = 'YESTERDAY'
          AND t.todayStatus = 'INCOMPLETE'
        ORDER BY t.todayOrder DESC
    """)
    List<Todo> findIncompleteYesterdays(@Param("userId") Long userId);

    @Query(value = """
        SELECT t.deadline AS date, COUNT(*) AS count
        FROM todo t
        WHERE t.user_id = :userId
          AND t.deadline IS NOT NULL
          AND t.deadline > CURDATE()
          AND YEAR(t.deadline) = :year
          AND MONTH(t.deadline) = :month
          AND t.type = 'BACKLOG'
        GROUP BY t.deadline
        ORDER BY t.deadline
    """, nativeQuery = true)
    List<Tuple> findDatesWithBacklogCount(Long userId, String year, int month);

    boolean existsByUserIdAndTypeAndTodayStatus(Long userId, Type type, TodayStatus todayStatus);

    @Query(value = """
    SELECT t.user_id AS userId, COALESCE(MAX(t.today_order), 0) AS maxOrder
    FROM todo t
    WHERE t.user_id IN (:userIds)
    GROUP BY t.user_id
    """, nativeQuery = true)
    List<Tuple> findMaxTodayOrdersByUserIds(@Param("userIds") List<Long> userIds);
}
