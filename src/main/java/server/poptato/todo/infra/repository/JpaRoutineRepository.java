package server.poptato.todo.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.poptato.todo.domain.entity.Routine;

import java.util.List;

public interface JpaRoutineRepository extends JpaRepository<Routine, Long> {

    @Modifying
    @Query("""
        DELETE FROM Routine r
        WHERE r.todoId = :todoId
        """)
    void deleteByTodoId(@Param("todoId") Long todoId);

    List<Routine> findAllByTodoId(Long todoId);
}
