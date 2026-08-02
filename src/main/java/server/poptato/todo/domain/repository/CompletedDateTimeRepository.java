package server.poptato.todo.domain.repository;

import server.poptato.todo.domain.entity.CompletedDateTime;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CompletedDateTimeRepository {

    /**
     * 특정 할 일의 해당 날짜 완료 기록을 모두 조회합니다.
     * <p>
     * 동시 요청 등으로 같은 날짜에 기록이 다건 쌓일 수 있어 단건이 아닌 목록으로 반환합니다.
     * todoId만으로 조회하므로 호출 전에 할 일의 소유권 검증이 선행되어야 합니다.
     */
    List<CompletedDateTime> findAllByTodoIdAndDate(Long todoId, LocalDate todayDate);

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
