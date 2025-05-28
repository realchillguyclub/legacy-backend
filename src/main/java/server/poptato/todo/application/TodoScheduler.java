package server.poptato.todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TodoScheduler {

    private final TodoBatchService todoBatchService;

    /**
     * 매일 새벽 특정 시간에 할 일 상태를 업데이트한다.
     */
    @Async
    @Scheduled(cron = "${scheduling.todoCron}")
    public void updateTodoType() {
        todoBatchService.updateTodayTodosAndSave();
        todoBatchService.updateDeadlineTodos();
    }
}
