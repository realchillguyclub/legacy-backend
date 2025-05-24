package server.poptato.todo.domain.repository;

import server.poptato.todo.domain.entity.TimeAlarm;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TimeAlarmRepository {

    Optional<TimeAlarm> findByTodoId(Long todoId);

    TimeAlarm save(TimeAlarm timeAlarm);

    void delete(TimeAlarm timeAlarm);

    List<TimeAlarm> findPushEnabledAlarms(LocalTime from, LocalTime to);

}
