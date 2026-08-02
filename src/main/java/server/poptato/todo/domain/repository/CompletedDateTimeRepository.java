package server.poptato.todo.domain.repository;

import server.poptato.todo.domain.entity.CompletedDateTime;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CompletedDateTimeRepository {

    List<CompletedDateTime> findAllByTodoIdAndDate(Long todoId, LocalDate todayDate);

    /**
     * 특정 할 일의 해당 날짜 완료 기록 중 가장 최근 1건을 조회합니다.
     * <p>
     * 하루에 여러 번 완료되어 기록이 다건 존재할 수 있으므로 단건 조회로 두면 예외가 발생합니다.
     * 미완료 토글 시 가장 마지막 완료 기록부터 제거하도록 최신 1건을 반환합니다.
     */
    default Optional<CompletedDateTime> findByTodoIdAndDate(Long todoId, LocalDate todayDate) {
        return findAllByTodoIdAndDate(todoId, todayDate).stream().findFirst();
    }

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
