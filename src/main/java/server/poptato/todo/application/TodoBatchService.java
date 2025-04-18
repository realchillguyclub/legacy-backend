package server.poptato.todo.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import server.poptato.global.util.BatchUtil;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.user.domain.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoBatchService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final TodoService todoService;

    @Value("${batch.todo.deadline-user-size}")
    private int batchSize;

    /**
     * 할 일의 상태(Type)를 업데이트하고 저장한다.
     */
    @Transactional
    public void updateTodayTodosAndSave() {
        Map<Long, List<Todo>> userIdAndTodaysMap = updateTodayTodos();
        saveUpdatedTodos(userIdAndTodaysMap);
    }

    /**
     * 오늘(TODAY) 상태의 할 일 중 완료 여부에 따라 상태를 변경한다.
     * 완료된 반복 할 일은 백로그로 이동하며, 미완료된 할 일은 어제로 변경된다.
     *
     * @return 사용자 ID별 오늘의 할 일 목록
     */
    private Map<Long, List<Todo>> updateTodayTodos() {
        Map<Long, List<Todo>> userIdAndTodayTodosMap = todoRepository.findByType(Type.TODAY)
                .stream()
                .collect(Collectors.groupingBy(Todo::getUserId));

        userIdAndTodayTodosMap.forEach((userId, todos) -> {
            int startingOrder = todoRepository.findMaxBacklogOrderByUserIdOrZero(userId) + 1;

            for (Todo todo : todos) {
                if (todo.getTodayStatus() == TodayStatus.COMPLETED && todo.isRepeat()) {
                    todo.setType(Type.BACKLOG);
                    todo.setTodayStatus(null);
                    todo.setTodayOrder(null);
                    todo.setBacklogOrder(startingOrder++);
                } else if (todo.getTodayStatus() == TodayStatus.INCOMPLETE) {
                    todo.setType(Type.YESTERDAY);
                    todo.setTodayOrder(null);
                    todo.setBacklogOrder(startingOrder++);
                }
            }
        });

        return userIdAndTodayTodosMap;
    }

    /**
     * 업데이트된 할 일을 한 번에 저장한다.
     */
    private void saveUpdatedTodos(Map<Long, List<Todo>> userIdAndTodaysMap) {
        userIdAndTodaysMap.values().stream()
                .flatMap(List::stream)
                .forEach(todoRepository::save);
    }

    /**
     * 마감기한이 된 할 일을 오늘 할 일(TODAY)로 변경한다.
     */
    public void updateDeadlineTodos() {
        LocalDate today = LocalDate.now();
        List<Long> userIds = userRepository.findAllUserIds();

        BatchUtil.splitIntoBatches(userIds, batchSize).forEach(batch -> {
            todoService.processUpdateDeadlineTodos(today, batch);
        });
    }
}
