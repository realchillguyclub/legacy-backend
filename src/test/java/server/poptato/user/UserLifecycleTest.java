package server.poptato.user;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.configuration.ControllerTestConfig;
import server.poptato.external.oauth.SocialUserInfo;
import server.poptato.todo.api.request.BacklogCreateRequestDto;
import server.poptato.todo.api.request.SwipeRequestDto;
import server.poptato.todo.application.TodoBacklogService;
import server.poptato.todo.application.TodoService;
import server.poptato.todo.application.response.BacklogCreateResponseDto;
import server.poptato.todo.domain.entity.CompletedDateTime;
import server.poptato.todo.domain.repository.CompletedDateTimeRepository;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.UserRepository;
import server.poptato.user.domain.value.MobileType;
import server.poptato.user.domain.value.SocialType;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserLifecycleTest extends ControllerTestConfig {

    @Autowired
    private TodoService todoService;

    @Autowired
    private TodoBacklogService todoBacklogService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompletedDateTimeRepository completedDateTimeRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deleteUser_shouldDeleteCompletedDateTimeData() throws Exception {
        // given
        LocalDateTime localDateTime = LocalDateTime.now();
        LoginRequestDto requestDto = new LoginRequestDto(SocialType.KAKAO, "valid-access-token", MobileType.ANDROID, "client-id");
        SocialUserInfo userInfo = new SocialUserInfo("0000000000", "Tester1", "test@naver.com", null);
        User user = User.create(requestDto, userInfo, null);
        User newUser = userRepository.save(user);
        Long userId = newUser.getId();

        BacklogCreateResponseDto backlog1 = todoBacklogService.generateBacklog(userId, new BacklogCreateRequestDto("테스트입니다.1", -1L));
        Long todoId1 = backlog1.todoId();
        todoService.swipe(userId, new SwipeRequestDto(todoId1));
        todoService.updateIsCompleted(userId, todoId1, localDateTime);

        BacklogCreateResponseDto backlog2 = todoBacklogService.generateBacklog(userId, new BacklogCreateRequestDto("테스트입니다.2", 0L));
        Long todoId2 = backlog2.todoId();
        todoService.swipe(userId, new SwipeRequestDto(todoId2));
        todoService.updateIsCompleted(userId, todoId2, localDateTime);

        //when
        userRepository.delete(newUser);
        entityManager.flush();
        entityManager.clear();

        //then
        Optional<CompletedDateTime> completedTodo1 = completedDateTimeRepository.findByDateAndTodoId(todoId1, localDateTime.toLocalDate());
        Assertions.assertTrue(completedTodo1.isEmpty(), "데이터가 삭제되었습니다.");
        Optional<CompletedDateTime> completedTodo2 = completedDateTimeRepository.findByDateAndTodoId(todoId2, localDateTime.toLocalDate());
        Assertions.assertTrue(completedTodo2.isEmpty(), "데이터가 삭제되었습니다.");
    }
}
