package server.poptato.todo.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.poptato.todo.domain.entity.TimeAlarm;
import server.poptato.todo.domain.repository.TimeAlarmRepository;

import java.time.LocalTime;
import java.util.List;

public interface JpaTimeAlarmRepository extends TimeAlarmRepository, JpaRepository<TimeAlarm, Long> {

    @Query("""
        SELECT ta
        FROM TimeAlarm ta
        JOIN User u ON ta.userId = u.id
        WHERE ta.todoTime BETWEEN :from AND :to
          AND ta.notified = false
          AND u.isPushAlarm = true
    """)
    List<TimeAlarm> findPushEnabledAlarms(@Param("from") LocalTime from, @Param("to") LocalTime to);

}
