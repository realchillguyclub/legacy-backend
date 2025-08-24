package server.poptato.todo.infra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import server.poptato.todo.application.response.RoutineCountDto;
import server.poptato.todo.domain.entity.Routine;

public interface JpaRoutineRepository extends JpaRepository<Routine, Long> {

	@Modifying
	@Query("DELETE FROM Routine r WHERE r.todoId = :todoId")
	void deleteByTodoId(@Param("todoId") Long todoId);

	List<Routine> findAllByTodoId(Long todoId);

	@Query(value = """
        SELECT r.day AS day, COUNT(*) AS count
        FROM routine r
        JOIN todo t ON t.id = r.todo_id
        WHERE t.user_id = :userId
          AND t.is_routine = 1
          AND t.type = 'BACKLOG'
        GROUP BY r.day
        """, nativeQuery = true)
	List<RoutineCountDto> countRoutinesByDay(@Param("userId") Long userId);
}
