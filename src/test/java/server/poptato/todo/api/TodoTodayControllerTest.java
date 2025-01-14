package server.poptato.todo.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server.poptato.auth.application.service.JwtService;
import server.poptato.todo.application.TodoTodayService;
import server.poptato.user.application.service.UserService;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TodoTodayControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TodoTodayService todoTodayService;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private RedisTemplate<String, String> redisTemplate;
    @MockBean
    private UserService userService;
    private String accessToken;
    private final String userId = "1";

    @BeforeEach
    void createAccessToken_UserIdOne() {
        accessToken = jwtService.createAccessToken(userId);
    }

    @AfterEach
    void deleteRefreshToken() {
        jwtService.deleteRefreshToken(userId);
    }


    @DisplayName("투데이 목록 조회 시 page와 size를 query string으로 받고 헤더에 accessToken을 담아 요청하면 성공한다.")
    @Test
    void getTodayList_Success() throws Exception {
        //given & when & then
        mockMvc.perform(get("/todays")
                        .param("page", "0")
                        .param("size", "8")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("투데이 목록 조회 시 Query String에 Default 값이 적용되고, JWT로 사용자 아이디를 조회한다.")
    @Test
    void getTodayList_Default_QueryString_Success() throws Exception {
        //given & when & then
        mockMvc.perform(get("/todays")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        LocalDate todayDate = LocalDate.now();

        verify(todoTodayService).getTodayList(1, 0, 8, todayDate);
    }

    @DisplayName("투데이 목록 조회 시 헤더에 JWT가 없으면 예외가 발생한다.")
    @Test
    void getTodayList_TokenNotExistException() throws Exception {
        //given & when & then
        mockMvc.perform(get("/todays")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}