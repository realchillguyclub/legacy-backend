package server.poptato.todo.domain.repository;

import feign.Param;
import server.poptato.todo.domain.entity.Routine;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;

public interface RoutineRepository {

    void deleteByTodoId(Long todoId);

    void saveAll(List<Routine> routineDays);

    List<Routine> findAllByTodoId(Long todoId);

	List<Map<String, Object>> countRoutinesByDay(@Param("userId") Long userId);
}
