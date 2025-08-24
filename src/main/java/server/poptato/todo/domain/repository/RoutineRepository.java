package server.poptato.todo.domain.repository;

import java.util.List;

import server.poptato.todo.application.response.RoutineCountDto;
import server.poptato.todo.domain.entity.Routine;

public interface RoutineRepository {

    void deleteByTodoId(Long todoId);

    void saveAll(List<Routine> routineDays);

    List<Routine> findAllByTodoId(Long todoId);

	List<RoutineCountDto> countRoutinesByDay(Long userId);
}
