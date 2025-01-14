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
import server.poptato.todo.api.request.BacklogCreateRequestDto;
import server.poptato.todo.application.TodoBacklogService;
import server.poptato.user.application.service.UserService;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TodoBacklogControllerTest {
    @Autowired
    private MockMvc mockMvc;
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

    @DisplayName("백로그 목록 조회 시 categoryId, page와 size를 query string으로 받고 헤더에 accessToken을 담아 요청하면 성공한다.")
    @Test
    void getBacklogList_Success() throws Exception {
        //given
        String categoryId = "-1";

        //when & then
        mockMvc.perform(get("/backlogs")
                        .param("category",categoryId)
                        .param("page", "0")
                        .param("size", "8")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("백로그 목록 조회 시 Query String에 Default 값이 적용되고, JWT로 사용자 아이디를 조회한다.")
    @Test
    void getBacklogList_Default_QueryString_Success() throws Exception {
        //given
        String categoryId = "-1";

        //when & then
        mockMvc.perform(get("/backlogs")
                        .param("category",categoryId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        verify(todoBacklogService).getBacklogList(1L, -1L, 0,8);
    }

    @DisplayName("백로그 목록 조회 시 헤더에 JWT가 없으면 예외가 발생한다.")
    @Test
    void getBacklogList_TokenNotExistException() throws Exception {
        //given
        String categoryId = "-1";

        //when & then
        mockMvc.perform(get("/backlogs")
                        .param("category",categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @DisplayName("백로그 목록 조회 시 categoryId가 없으면 예외가 발생한다.")
    @Test
    void getBacklogList_NoPathVariableException() throws Exception {
        //given & when & then
        mockMvc.perform(get("/backlogs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }


    @DisplayName("백로그 생성 시 contentId와 content가 없거나 비어있으면 Validator가 예외를 발생한다.")
    @Test
    void generateBacklog_ValidatorException() {
        //given
        String emptyContent = " ";
        Long nullCategoryId = null;
        String nullContent = null;
        BacklogCreateRequestDto emptyContentCreateRequestDto = BacklogCreateRequestDto.builder()
                .categoryId(nullCategoryId)
                .content(emptyContent)
                .build();

        BacklogCreateRequestDto nullContentCreateRequestDto = BacklogCreateRequestDto.builder()
                .content(nullContent)
                .categoryId(nullCategoryId)
                .build();

        //when
        Set<ConstraintViolation<BacklogCreateRequestDto>> emptyViolations = validator.validate(emptyContentCreateRequestDto);
        Set<ConstraintViolation<BacklogCreateRequestDto>> nullViolations = validator.validate(nullContentCreateRequestDto);

        //then
        Assertions.assertEquals(emptyViolations.size(), 2);
        Assertions.assertEquals(nullViolations.size(), 2);
    }

    @DisplayName("백로그 생성 시 성공한다.")
    @Test
    void generateBacklog_Success() throws Exception {
        //when
        mockMvc.perform(post("/backlog")
                        .content("{\"categoryId\": \"1\", \"content\": \"할일 내용 수정\"}")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}