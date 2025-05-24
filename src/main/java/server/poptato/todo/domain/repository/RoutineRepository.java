package server.poptato.todo.domain.repository;

import server.poptato.todo.domain.entity.Routine;

import java.util.List;

public interface RoutineRepository {

    void deleteByTodoId(Long todoId);

    void saveAll(List<Routine> routines);

    List<Routine> findAllByTodoId(Long todoId);
}
