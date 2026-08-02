package server.poptato.todo.infra;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import server.poptato.configuration.DatabaseTestConfig;
import server.poptato.configuration.MySqlDataJpaTest;
import server.poptato.todo.domain.entity.CompletedDateTime;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.todo.infra.repository.JpaTodoRepository;

@MySqlDataJpaTest
public class JpaTodoRepositoryTest extends DatabaseTestConfig {

    @Autowired
    private JpaTodoRepository jpaTodoRepository;

    private Todo createTodo(Long userId, Long categoryId, String content) {
        Todo todo = Todo.builder()
                .userId(userId)
                .categoryId(categoryId)
                .type(Type.BACKLOG)
                .content(content)
                .todayStatus(TodayStatus.INCOMPLETE)
                .build();
        tem.persist(todo);
        tem.flush();
        tem.clear();
        return todo;
    }

    private void createCompletedDateTime(Long todoId, LocalDateTime dateTime) {
        tem.persist(CompletedDateTime.builder()
                .todoId(todoId)
                .dateTime(dateTime)
                .build());
        tem.flush();
        tem.clear();
    }

    private Boolean getIsDeleted(Long id) {
        Object result = tem.getEntityManager().createNativeQuery(
                "SELECT is_deleted FROM todo WHERE id = :id"
        ).setParameter("id", id).getSingleResult();
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return ((Number) result).intValue() == 1;
    }

    @Nested
    @DisplayName("[SCN-REP-TODO-001] Soft Delete 테스트")
    @TestMethodOrder(MethodOrderer.DisplayName.class)
    class SoftDeleteTests {

        @Test
        @DisplayName("[TC-SOFT-DELETE-001] softDeleteByUserId 호출 시 해당 유저의 모든 Todo가 soft delete 된다")
        void softDeleteByUserId_deletesAllUserTodos() {
            // given
            Long userId = 100L;
            Long otherUserId = 200L;

            Todo todo1 = createTodo(userId, null, "todo1");
            Todo todo2 = createTodo(userId, null, "todo2");
            Todo otherTodo = createTodo(otherUserId, null, "other");

            // when
            jpaTodoRepository.softDeleteByUserId(userId);
            tem.flush();
            tem.clear();

            // then - Native Query로 is_deleted 값 직접 확인 (@SQLRestriction 우회)
            assertThat(getIsDeleted(todo1.getId())).isTrue();
            assertThat(getIsDeleted(todo2.getId())).isTrue();
            assertThat(getIsDeleted(otherTodo.getId())).isFalse();
        }

        @Test
        @DisplayName("[TC-SOFT-DELETE-002] softDeleteByCategoryId 호출 시 해당 카테고리의 모든 Todo가 soft delete 된다")
        void softDeleteByCategoryId_deletesAllCategoryTodos() {
            // given
            Long userId = 100L;
            Long categoryId = 10L;
            Long otherCategoryId = 20L;

            Todo todo1 = createTodo(userId, categoryId, "todo1");
            Todo todo2 = createTodo(userId, categoryId, "todo2");
            Todo otherTodo = createTodo(userId, otherCategoryId, "other");

            // when
            jpaTodoRepository.softDeleteByCategoryId(categoryId);
            tem.flush();
            tem.clear();

            // then - Native Query로 is_deleted 값 직접 확인 (@SQLRestriction 우회)
            assertThat(getIsDeleted(todo1.getId())).isTrue();
            assertThat(getIsDeleted(todo2.getId())).isTrue();
            assertThat(getIsDeleted(otherTodo.getId())).isFalse();
        }

        @Test
        @DisplayName("[TC-SOFT-DELETE-003] soft delete된 Todo는 findById로 조회되지 않는다")
        void findById_excludesSoftDeletedTodo() {
            // given
            Long userId = 300L;
            Todo todo = createTodo(userId, null, "deleted-todo");
            Long todoId = todo.getId();

            jpaTodoRepository.softDeleteByUserId(userId);
            tem.flush();
            tem.clear();

            // when
            var found = jpaTodoRepository.findById(todoId);

            // then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("[TC-SOFT-DELETE-004] soft delete된 Todo는 findMaxBacklogOrderByUserIdOrZero에서 제외된다")
        void findMaxBacklogOrderByUserIdOrZero_excludesSoftDeletedTodo() {
            // given
            Long userId = 400L;

            Todo todo1 = Todo.builder()
                    .userId(userId)
                    .type(Type.BACKLOG)
                    .content("todo1")
                    .backlogOrder(5)
                    .build();
            tem.persist(todo1);

            Todo todo2 = Todo.builder()
                    .userId(userId)
                    .type(Type.BACKLOG)
                    .content("todo2")
                    .backlogOrder(10)
                    .build();
            tem.persist(todo2);
            tem.flush();
            tem.clear();

            // soft delete 전 maxOrder = 10
            Integer beforeDelete = jpaTodoRepository.findMaxBacklogOrderByUserIdOrZero(userId);
            assertThat(beforeDelete).isEqualTo(10);

            // when - backlogOrder 10인 todo만 soft delete
            tem.getEntityManager().createNativeQuery(
                    "UPDATE todo SET is_deleted = true WHERE user_id = :userId AND backlog_order = 10"
            ).setParameter("userId", userId).executeUpdate();
            tem.flush();
            tem.clear();

            // then - soft delete 후 maxOrder = 5
            Integer afterDelete = jpaTodoRepository.findMaxBacklogOrderByUserIdOrZero(userId);
            assertThat(afterDelete).isEqualTo(5);
        }

        @Test
        @DisplayName("[TC-SOFT-DELETE-005] soft delete된 Todo는 findAllBacklogs에서 제외된다")
        void findAllBacklogs_excludesSoftDeletedTodo() {
            // given
            Long userId = 500L;

            Todo todo1 = Todo.builder()
                    .userId(userId)
                    .type(Type.BACKLOG)
                    .content("active-todo")
                    .backlogOrder(1)
                    .todayStatus(TodayStatus.INCOMPLETE)
                    .build();
            tem.persist(todo1);

            Todo todo2 = Todo.builder()
                    .userId(userId)
                    .type(Type.BACKLOG)
                    .content("deleted-todo")
                    .backlogOrder(2)
                    .todayStatus(TodayStatus.INCOMPLETE)
                    .build();
            tem.persist(todo2);
            tem.flush();
            tem.clear();

            // todo2만 soft delete
            tem.getEntityManager().createNativeQuery(
                    "UPDATE todo SET is_deleted = true WHERE user_id = :userId AND content = 'deleted-todo'"
            ).setParameter("userId", userId).executeUpdate();
            tem.flush();
            tem.clear();

            // when
            var page = jpaTodoRepository.findAllBacklogs(
                    userId,
                    Type.BACKLOG,
                    TodayStatus.COMPLETED,
                    org.springframework.data.domain.PageRequest.of(0, 10)
            );

            // then - active-todo만 조회됨
            assertThat(page.getContent()).hasSize(1);
            assertThat(page.getContent().get(0).getContent()).isEqualTo("active-todo");
        }
    }

    @Nested
    @DisplayName("[SCN-REP-TODO-002] 히스토리 조회 테스트")
    @TestMethodOrder(MethodOrderer.DisplayName.class)
    class FindHistoriesTests {

        @Test
        @DisplayName("[TC-HISTORY-001] 같은 날짜에 완료 기록이 여러 건이어도 히스토리 조회에 성공한다")
        void findHistories_duplicatedCompletedDateTime_returnsTodoOnce() {
            // given - 하나의 할 일에 같은 날짜의 완료 기록이 3건 존재
            Long userId = 300L;
            LocalDate completedDate = LocalDate.of(2025, 9, 30);
            Todo todo = createTodo(userId, null, "중복 완료 할 일");
            createCompletedDateTime(todo.getId(), completedDate.atTime(2, 18, 56));
            createCompletedDateTime(todo.getId(), completedDate.atTime(10, 0, 0));
            createCompletedDateTime(todo.getId(), completedDate.atTime(18, 30, 0));

            // when
            var page = jpaTodoRepository.findHistories(userId, completedDate, PageRequest.of(0, 10));

            // then - 다건이어도 예외 없이 할 일이 한 번만 조회됨
            assertThat(page.getContent()).hasSize(1);
            assertThat(page.getContent().get(0).getId()).isEqualTo(todo.getId());
        }

        @Test
        @DisplayName("[TC-HISTORY-002] 히스토리는 그날의 첫 완료 시각 오름차순으로 정렬된다")
        void findHistories_multipleTodos_sortedByFirstCompletedTime() {
            // given - 중복 기록을 가진 할 일이 단건 할 일의 완료 시각 앞뒤에 걸치도록 구성
            //         (첫 완료 기준이면 duplicated가 먼저, 마지막 완료 기준이면 single이 먼저 정렬됨)
            Long userId = 301L;
            LocalDate completedDate = LocalDate.of(2025, 10, 2);
            Todo duplicated = createTodo(userId, null, "중복 기록 할 일");
            Todo single = createTodo(userId, null, "단건 기록 할 일");
            createCompletedDateTime(duplicated.getId(), completedDate.atTime(8, 0, 0));
            createCompletedDateTime(duplicated.getId(), completedDate.atTime(20, 0, 0));
            createCompletedDateTime(single.getId(), completedDate.atTime(12, 0, 0));

            // when
            var page = jpaTodoRepository.findHistories(userId, completedDate, PageRequest.of(0, 10));

            // then - 마지막 완료 시각(20:00)이 아니라 첫 완료 시각(08:00) 기준으로 정렬됨
            assertThat(page.getContent()).hasSize(2);
            assertThat(page.getContent().get(0).getId()).isEqualTo(duplicated.getId());
            assertThat(page.getContent().get(1).getId()).isEqualTo(single.getId());
        }
    }
}
