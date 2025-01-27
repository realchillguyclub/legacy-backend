package server.poptato.todo.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.poptato.todo.domain.entity.CompletedDateTime;
import server.poptato.todo.domain.repository.CompletedDateTimeRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JpaCompletedDateTimeRepository extends CompletedDateTimeRepository, JpaRepository<CompletedDateTime,Long> {
    @Query("""
    SELECT c
    FROM CompletedDateTime c 
    WHERE c.todoId = :todoId AND FUNCTION('DATE', c.dateTime) = :todayDate
    """)
    Optional<CompletedDateTime> findByDateAndTodoId(@Param("todoId") Long todoId, @Param("todayDate") LocalDate todayDate);

    @Query(value = "SELECT DISTINCT c.date_time " +
            "FROM completed_date_time c " +
            "JOIN todo t ON c.todo_id = t.id " +
            "WHERE t.user_id = :userId " +
            "AND YEAR(c.date_time) = :year " +
            "AND MONTH(c.date_time) = :month",
            nativeQuery = true)
    List<Timestamp> findDistinctCompletedDateTimesByUserIdAndYearMonth(
            @Param("userId") Long userId,
            @Param("year") String year,
            @Param("month") int month
    );
}
