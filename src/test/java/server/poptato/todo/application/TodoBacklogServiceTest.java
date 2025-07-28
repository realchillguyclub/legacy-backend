package server.poptato.todo.application;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import server.poptato.category.validator.CategoryValidator;
import server.poptato.todo.api.request.BacklogCreateRequestDto;
import server.poptato.todo.application.response.BacklogCreateResponseDto;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.user.validator.UserValidator;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TodoBacklogServiceTest {

    private static final Long ALL_CATEGORY_ID = -1L;
    private static final Long BOOKMARK_CATEGORY_ID = 0L;
    private static final Long GENERAL_CATEGORY_ID = 2147483647L;

    @Mock
    private UserValidator userValidator;

    @Mock
    private CategoryValidator categoryValidator;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoBacklogService todoBacklogService;

    @BeforeEach
    void beforeEach() {

    }

    @ParameterizedTest
    @MethodSource("카테고리별_입력")
    @DisplayName("카테고리별(전체, 중요, 사용자 정의 카테고리)로 할 일을 생성하고 id를 반환한다.")
    void createBacklog_할_일_생성(Long categoryId, boolean isBookmark, boolean hasCategory) {
        BacklogCreateResponseDto response = 할_일_생성_helper(categoryId, isBookmark, hasCategory);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(999L, response.todoId());
    }

    private static Stream<Arguments> 카테고리별_입력() {
        return Stream.of(
                Arguments.of(ALL_CATEGORY_ID, false, false),  // ALL_CATEGORY
                Arguments.of(BOOKMARK_CATEGORY_ID, true, false),   // BOOKMARK_CATEGORY
                Arguments.of(GENERAL_CATEGORY_ID, false, true)   // 일반 카테고리
        );
    }

    private BacklogCreateResponseDto 할_일_생성_helper(Long categoryId, boolean expectBookmark, boolean expectHasCategory) {
        //given
        Long userId = 1L;
        Long todoId = 999L;
        String content = "백로그 생성 테스트";
        int maxOrder = 10;
        BacklogCreateRequestDto requestDto = new BacklogCreateRequestDto(content, categoryId);

        Mockito.doNothing().when(userValidator).checkIsExistUser(userId);
        Mockito.doNothing().when(categoryValidator).validateCategory(userId, categoryId);
        Mockito.when(todoRepository.findMaxBacklogOrderByUserIdOrZero(userId)).thenReturn(maxOrder);
        Mockito.when(todoRepository.save(any())).thenAnswer(invocation -> {
           Todo todo = invocation.getArgument(0);
           ReflectionTestUtils.setField(todo, "id", todoId);
           return todo;
        });

        //when
        BacklogCreateResponseDto responseDto = todoBacklogService.createBacklog(userId, requestDto);

        //then
        Mockito.verify(userValidator).checkIsExistUser(userId);
        Mockito.verify(categoryValidator).validateCategory(userId, categoryId);
        Mockito.verify(todoRepository).findMaxBacklogOrderByUserIdOrZero(userId);

        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        Mockito.verify(todoRepository).save(captor.capture());

        Todo saved = captor.getValue();

        Assertions.assertEquals(userId, saved.getUserId());
        Assertions.assertEquals(content, saved.getContent());
        Assertions.assertEquals(maxOrder + 1, saved.getBacklogOrder().intValue());
        Assertions.assertEquals(expectBookmark, saved.isBookmark());
        if (expectHasCategory) {
            Assertions.assertEquals(categoryId, saved.getCategoryId());
        } else {
            Assertions.assertNull(saved.getCategoryId());
        }

        return responseDto;
    }



    @Test
    void 할_일_생성_유효하지_않은_유저() {

    }

    @Test
    void 할_일_생성_유효하지_않은_카테고리() {

    }

    @Test
    void 할_일_생성_전체_카테고리() {

    }

    @Test
    void 할_일_생성_중요_카테고리() {

    }

    @Test
    void 할_일_생성_나머지_카테고리() {

    }

}