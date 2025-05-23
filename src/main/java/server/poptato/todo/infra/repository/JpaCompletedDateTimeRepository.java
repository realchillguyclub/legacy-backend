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

public interface JpaCompletedDateTimeRepository extends CompletedDateTimeRepository, JpaRepository<CompletedDateTime, Long> {

    @Query("""
    SELECT c
    FROM CompletedDateTime c 
    WHERE c.todoId = :todoId
      AND FUNCTION('DATE', c.createDate) = :createDate
    """)
    Optional<CompletedDateTime> findByCreateDateAndTodoId(
            @Param("todoId") Long todoId,
            @Param("createDate") LocalDate createDate
    );

    @Query(value = """
    SELECT DISTINCT c.create_date
    FROM completed_date_time c
    JOIN todo t ON c.todo_id = t.id
    WHERE t.user_id = :userId
      AND YEAR(c.create_date) = :year
      AND MONTH(c.create_date) = :month
    """, nativeQuery = true)
    List<Timestamp> findDistinctCompletedDateTimesByUserIdAndYearMonth(
            @Param("userId") Long userId,
            @Param("year") String year,
            @Param("month") int month
    );
}
