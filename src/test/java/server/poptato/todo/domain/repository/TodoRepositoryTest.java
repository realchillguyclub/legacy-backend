package server.poptato.todo.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import server.poptato.todo.domain.entity.CompletedDateTime;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class TodoRepositoryTest {
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private CompletedDateTimeRepository completedDateTimeRepository;

    @DisplayName("달성된 투데이 조회 시, userId가 1이 등록한 달성된 투데이가 달성 시각 순서에 따라 성공적으로 정렬되어 조회된다.")
    @Test
    void findCompletedToday_Success() {
        //given
        Long userId = 1L;
        LocalDate todayDate = LocalDate.of(2024, 10, 16);

        //when
        List<Todo> todos = todoRepository.findCompletedTodayByUserIdOrderByCompletedDateTimeAsc(
                userId, todayDate);

        //then
        assertThat(todos).isNotEmpty();
        assertThat(todos.stream().allMatch(todo -> todo.getUserId().equals(userId))).isTrue();
        assertThat(todos.stream().allMatch(todo -> todo.getType() == Type.TODAY)).isTrue();
        assertThat(todos.stream().allMatch(todo -> todo.getTodayDate().equals(todayDate))).isTrue();
        for (int i = 0; i < todos.size() - 1; i++) {
            CompletedDateTime currentCompletionTime = completedDateTimeRepository.findByDateAndTodoId(todos.get(i).getId(), todayDate).get();
            CompletedDateTime nextCompletionTime = completedDateTimeRepository.findByDateAndTodoId(todos.get(i + 1).getId(), todayDate).get();

            assertThat(currentCompletionTime.getDateTime()).isBeforeOrEqualTo(nextCompletionTime.getDateTime()); // 정렬 확인
        }
    }

    @DisplayName("미달성된 투데이 조회 시, userId가 1이 등록한 미달성 투데이가 TodayOrder에 따라 성공적으로 내림차순 정렬되어 조회된다.")
    @Test
    void findInCompleteToday_Success() {
        //given
        Long userId = 1L;
        LocalDate todayDate = LocalDate.of(2024, 10, 16);

        //when
        List<Todo> todos = todoRepository.findIncompleteTodays(userId, Type.TODAY, todayDate, TodayStatus.INCOMPLETE);

        //then
        assertThat(todos).isNotEmpty();
        assertThat(todos.stream().allMatch(todo -> todo.getUserId().equals(userId))).isTrue();
        assertThat(todos.stream().allMatch(todo -> todo.getType() == Type.TODAY)).isTrue();
        assertThat(todos.stream().allMatch(todo -> todo.getTodayDate().equals(todayDate))).isTrue();
        for (int i = 0; i < todos.size() - 1; i++) {
            assertThat(todos.get(i).getTodayOrder()).isGreaterThan(todos.get(i + 1).getTodayOrder());
        }
    }

    @DisplayName("전체 백로그 목록 조회 시, userId가 1이 등록한 백로그 리스트가 BacklogOrder에 따라 성공적으로 내림차순 정렬되어 조회된다.")
    @Test
    void findAllBacklogs_Success() {
        //given
        Long userId = 1L;
        List<Type> types = List.of(Type.BACKLOG, Type.YESTERDAY);
        TodayStatus status = TodayStatus.COMPLETED;
        PageRequest pageRequest = PageRequest.of(0, 8);

        //when
        Page<Todo> backlogs = todoRepository.findAllBacklogs(userId, types, status, pageRequest);

        //then
        assertThat(backlogs.getContent()).isNotEmpty();
        assertThat(backlogs.getContent().stream().allMatch(backlog -> backlog.getUserId().equals(userId))).isTrue();
        assertThat(backlogs.getContent().stream().allMatch(backlog -> backlog.getType().equals(Type.BACKLOG)  || backlog.getType().equals(Type.YESTERDAY))).isTrue();
        for (int i = 0; i < backlogs.getContent().size() - 1; i++) {
            assertThat(backlogs.getContent().get(i).getBacklogOrder()).isGreaterThan(backlogs.getContent().get(i + 1).getBacklogOrder());
        }
    }

    @DisplayName("북마크한 백로그 목록 조회 시, userId가 1이 등록한 백로그 리스트가 BacklogOrder에 따라 성공적으로 내림차순 정렬되어 조회된다.")
    @Test
    void findBookmarkBacklogs_Success() {
        //given
        Long userId = 1L;
        List<Type> types = List.of(Type.BACKLOG, Type.YESTERDAY);
        TodayStatus status = TodayStatus.COMPLETED;
        PageRequest pageRequest = PageRequest.of(0, 8);

        //when
        Page<Todo> backlogs = todoRepository.findBookmarkBacklogs(userId, types, status, pageRequest);

        //then
        assertThat(backlogs.getContent()).isNotEmpty();
        assertThat(backlogs.getContent().stream().allMatch(backlog -> backlog.getUserId().equals(userId))).isTrue();
        assertThat(backlogs.getContent().stream().allMatch(backlog -> backlog.getType().equals(Type.BACKLOG)  || backlog.getType().equals(Type.YESTERDAY))).isTrue();
        for (int i = 0; i < backlogs.getContent().size() - 1; i++) {
            assertThat(backlogs.getContent().get(i).isBookmark()).isTrue();
            assertThat(backlogs.getContent().get(i).getBacklogOrder()).isGreaterThan(backlogs.getContent().get(i + 1).getBacklogOrder());
        }
    }

    @DisplayName("카테고리 지정 백로그 목록 조회 시, userId가 1이 등록한 백로그 리스트가 CategoryId와 BacklogOrder에 따라 성공적으로 내림차순 정렬되어 조회된다.")
    @Test
    void findBacklogsByCategoryId_Success() {
        //given
        Long userId = 1L;
        List<Type> types = List.of(Type.BACKLOG, Type.YESTERDAY);
        TodayStatus status = TodayStatus.COMPLETED;
        PageRequest pageRequest = PageRequest.of(0, 8);
        Long categoryId = 1L;

        //when
        Page<Todo> backlogs = todoRepository.findBacklogsByCategoryId(userId, categoryId, types, status, pageRequest);

        //then
        assertThat(backlogs.getContent()).isNotEmpty();
        assertThat(backlogs.getContent().stream().allMatch(backlog -> backlog.getUserId().equals(userId))).isTrue();
        assertThat(backlogs.getContent().stream().allMatch(backlog -> backlog.getType().equals(Type.BACKLOG)  || backlog.getType().equals(Type.YESTERDAY))).isTrue();
        for (int i = 0; i < backlogs.getContent().size() - 1; i++) {
            assertThat(backlogs.getContent().get(i).getCategoryId()).isEqualTo(categoryId);
            assertThat(backlogs.getContent().get(i).getBacklogOrder()).isGreaterThan(backlogs.getContent().get(i + 1).getBacklogOrder());
        }
    }

    @DisplayName("히스토리 조회 시,userId가 1의 기록을 조회한다")
    @Test
    void findHistories_Success(){
        //given
        Long userId = 1L;
        LocalDate date = LocalDate.of(2024,10,16);

        Pageable pageable = PageRequest.of(0, 15);

        //when
        Page<Todo> histories = todoRepository.findHistories(userId, date, pageable);

        //then
        assertThat(histories).isNotNull();
        assertThat(histories.getContent().size()).isEqualTo(6);
    }
}