package server.poptato.todo.infra.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import server.poptato.todo.domain.entity.Routine;
import server.poptato.todo.domain.repository.RoutineRepository;
import server.poptato.todo.infra.repository.JpaRoutineRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoutineRepositoryImpl implements RoutineRepository {

    private final JpaRoutineRepository jpaRoutineRepository;

    @Override
    public void deleteByTodoId(Long todoId) {
        jpaRoutineRepository.deleteByTodoId(todoId);
    }

    @Override
    public void saveAll(List<Routine> routineDays) {
        jpaRoutineRepository.saveAll(routineDays);
    }

    @Override
    public List<Routine> findAllByTodoId(Long todoId) {
        return jpaRoutineRepository.findAllByTodoId(todoId);
    }
}
