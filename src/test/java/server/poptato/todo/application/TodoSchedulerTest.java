package server.poptato.todo.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.Type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
class TodoSchedulerTest {
    @Autowired
    TodoRepository todoRepository;
    @Autowired
    TodoScheduler todoScheduler;

    @Test
    @DisplayName("스케줄러가 매일 자정에 성공적으로 실행된다.")
    public void scheduler_cron_Success() throws ParseException {
        //given
        String cronExpression = "0 0 0 * * *";
        CronTrigger trigger = new CronTrigger(cronExpression);
        Date startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2023/12/19 23:59:50");
        SimpleTriggerContext context = new SimpleTriggerContext();
        context.update(startTime, startTime, startTime);
        String expectedTime = "2023/12/20 00:00:00";
        Date nextExecutionTime = trigger.nextExecutionTime(context);

        //when & then
        String actualTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(nextExecutionTime);
        Assertions.assertThat(actualTime).isEqualTo(expectedTime);
        context.update(nextExecutionTime, nextExecutionTime, nextExecutionTime);
    }

    @Test
    @DisplayName("updateTodoType 메서드가 성공적으로 실행된다.")
    void updateTodoType_Success() {
        //when
        todoScheduler.updateTodoType();

        //then
        List<Todo> yesterdayTasks = todoRepository.findByType(Type.YESTERDAY);
        List<Long> expectedYesterdays = List.of(1L, 2L, 4L, 5L, 7L, 8L, 10L, 12L, 14L, 16L, 38L, 39L);
        assertTasksContainIds(yesterdayTasks, expectedYesterdays);

        List<Todo> backlogTasks = todoRepository.findByType(Type.BACKLOG);
        List<Long> expectedBacklogs = List.of(3L, 6L, 27L, 28L, 29L, 30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L);
        assertTasksContainIds(backlogTasks, expectedBacklogs);
    }

    private void assertTasksContainIds(List<Todo> tasks, List<Long> expectedIds) {
        List<Long> taskIds = tasks.stream()
                .map(Todo::getId)
                .toList();
        assertTrue(taskIds.containsAll(expectedIds),
                () -> "다음 할 일이 포함되어있지 않습니다: " +
                        expectedIds.stream()
                                .filter(content -> !taskIds.contains(content))
                                .toList());
    }
}