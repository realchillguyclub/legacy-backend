package server.poptato.todo.domain.repository;

import server.poptato.todo.domain.entity.CompletedDateTime;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CompletedDateTimeRepository {
    Optional<CompletedDateTime> findByDateAndTodoId(Long id, LocalDate todayDate);
    boolean existsByDateTimeAndTodoId(LocalDateTime dateTime, Long todoId);
    void delete(CompletedDateTime completedDateTime);
    CompletedDateTime save(CompletedDateTime completedDateTime);
    List<Timestamp> findDistinctCompletedDateTimesByUserIdAndYearMonth(Long userId, String year, int month);

    default List<LocalDateTime> findHistoryExistingDates(Long userId, String year, int month) {
        List<Timestamp> timestamps = findDistinctCompletedDateTimesByUserIdAndYearMonth(userId, year, month);
        return timestamps.stream()
                .map(Timestamp::toLocalDateTime)
                .toList();
    }
}
