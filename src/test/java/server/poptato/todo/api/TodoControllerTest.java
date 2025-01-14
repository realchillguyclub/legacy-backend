package server.poptato.todo.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server.poptato.auth.application.service.JwtService;
import server.poptato.todo.api.request.ContentUpdateRequestDto;
import server.poptato.todo.api.request.TodoDragAndDropRequestDto;
import server.poptato.todo.api.request.SwipeRequestDto;
import server.poptato.todo.application.TodoBacklogService;
import server.poptato.todo.application.TodoService;
import server.poptato.user.application.service.UserService;

import java.util.ArrayList;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TodoService todoService;
    @MockBean
    private TodoBacklogService todoBacklogService;
    @MockBean
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private RedisTemplate<String, String> redisTemplate;
    private Validator validator;
    private String accessToken;
    private final String userId = "1";

    @BeforeEach
    void createAccessToken_And_SetValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        accessToken = jwtService.createAccessToken(userId);
    }

    @AfterEach
    void deleteRefreshToken() {
        jwtService.deleteRefreshToken(userId);
    }

    @DisplayName("할 일 내용 수정 시 성공한다.")
    @Test
    void updateContent_Success() throws Exception {
        //given
        Long todoId = 1L;

        //when & then
        mockMvc.perform(patch("/todo/{todoId}/content", todoId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content("{\"content\": \"내용 수정\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("할 일 내용 수정 시 content가 null이거나 비어있으면 검증기가 예외를 발생한다.")
    @Test
    void updateContent_Validator_Exception() {
        //given
        String emptyContent = " ";
        String nullContent = null;
        ContentUpdateRequestDto emptyContentUpdateRequestDto = ContentUpdateRequestDto.builder()
                .content(emptyContent)
                .build();

        ContentUpdateRequestDto nullContentUpdateRequestDto = ContentUpdateRequestDto.builder()
                .content(nullContent)
                .build();
        //when
        Set<ConstraintViolation<ContentUpdateRequestDto>> emptyViolations = validator.validate(emptyContentUpdateRequestDto);
        Set<ConstraintViolation<ContentUpdateRequestDto>> nullViolations = validator.validate(nullContentUpdateRequestDto);

        //then
        Assertions.assertEquals(emptyViolations.size(), 1);
        Assertions.assertEquals(nullViolations.size(), 1);
    }

    @DisplayName("할 일 삭제 시 성공한다")
    @Test
    void delete_Success() throws Exception {
        //given
        Long todoId = 1L;

        //when & then
        mockMvc.perform(delete("/todo/{todoId}", todoId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("즐겨찾기 변경 시 응답이 정상적으로 반환되는지 확인")
    void toggleIsBookmark_Success() throws Exception {
        //given
        Long todoId = 1L;
        Long userId = 1L;

        //when & then
        mockMvc.perform(patch("/todo/{todoId}/bookmark", todoId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @DisplayName("스와이프 시 요청 바디에 todoId가 없으면 Validator가 예외를 발생한다.")
    @Test
    void swipe_ValidatorException() {
        //given
        SwipeRequestDto request = SwipeRequestDto.builder()
                .todoId(null).build();

        //when
        Set<ConstraintViolation<SwipeRequestDto>> violations = validator.validate(request);

        //then
        Assertions.assertEquals(violations.size(), 1);
    }

    @DisplayName("스와이프 요청 시 성공한다.")
    @Test
    void swipe_Success() throws Exception {
        //given & when & then
        mockMvc.perform(patch("/swipe")
                        .content("{\"todoId\": 1}")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("드래그앤드롭 요청 시 성공한다.")
    @Test
    void dragAndDrop_Success() throws Exception {
        //given & when & then
        mockMvc.perform(patch("/todo/dragAndDrop")
                        .content("{\"type\": \"TODAY\", \"todoIds\": [1, 2, 3, 4]}")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("드래그앤드롭 시 요청 바디에 type이나 list가 없으면 Validator가 예외를 발생한다.")
    @Test
    void dragAndDrop_ValidatorException() {
        //given
        TodoDragAndDropRequestDto todoDragAndDropRequestDto = TodoDragAndDropRequestDto.builder()
                .type(null)
                .todoIds(new ArrayList<>())
                .build();

        //when
        Set<ConstraintViolation<TodoDragAndDropRequestDto>> violations = validator.validate(todoDragAndDropRequestDto);

        //then
        Assertions.assertEquals(violations.size(), 2);
    }

    @DisplayName("할 일 상세 정보 요청 시 성공한다.")
    @Test
    void getTodoInfo_Success() throws Exception {
        //given
        Long todoId = 1L;

        //when
        mockMvc.perform(get("/todo/{todoId}", todoId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("마감기한 수정 요청 시 성공한다.")
    @Test
    void updateDeadline_Success() throws Exception {
        //given
        Long todoId = 1L;

        //when
        mockMvc.perform(patch("/todo/{todoId}/deadline", todoId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content("{\"deadline\": \"2024-12-25\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("할 일 달성여부 변경 요청 시 성공한다.")
    @Test
    void updateIsCompleted_Success() throws Exception {
        //given
        Long todoId = 1L;

        //when
        mockMvc.perform(patch("/todo/{todoId}/achieve", todoId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @DisplayName("히스토리 목록 조회 시 page와 size를 query string으로 받고 헤더에 accessToken을 담아 요청한다.")
    @Test
    void getHistories_Success() throws Exception {
        // given & when & then
        mockMvc.perform(get("/histories")
                        .param("page", "0")
                        .param("size", "15")
                        .param("date", "2024-10-16")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
    @DisplayName("캘린더 조회시 year랑 month를 query string 으로 받고 헤더에 accessToken을 담아 요청한다.")
    @Test
    void getCalendar_Success() throws Exception {
        // given & when & then
        mockMvc.perform(get("/calendar")
                        .param("year", "2024")
                        .param("month", "10")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}
