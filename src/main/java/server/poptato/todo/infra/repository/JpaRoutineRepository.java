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

	@Query("""
        SELECT new server.poptato.todo.application.response.RoutineCountDto(r.day, COUNT(r))
        FROM Routine r
        WHERE r.todoId IN (
            SELECT t.id
            FROM Todo t
            WHERE t.userId = :userId
              AND t.isRoutine = true
              AND t.type = 'BACKLOG'
        )
        GROUP BY r.day
        """)
	List<RoutineCountDto> countRoutinesByDay(@Param("userId") Long userId);
}
