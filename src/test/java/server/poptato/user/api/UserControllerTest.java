package server.poptato.user.api;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import server.poptato.auth.application.service.JwtService;
import server.poptato.todo.application.TodoService;
import server.poptato.user.application.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TodoService todoService;
    @MockBean
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private RedisTemplate<String, String> redisTemplate;
    private String accessToken;
    private final String userId = "1";

    @BeforeEach
    void createAccessToken_UserIdIsOne() {
        accessToken = jwtService.createAccessToken(userId);
    }

    @AfterEach
    void deleteRefreshToken() {
        jwtService.deleteRefreshToken(userId);
    }

    @Test
    @DisplayName("마이페이지 조회 시 성공한다.")
    void getUserInfo_Success() throws Exception {
        //given & when & then
        mockMvc.perform(get("/user/mypage")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("마이페이지 조회 시 유효하지 않은 토큰이면 예외가 발생한다.")
    void getUserInfo_InvalidTokenException() throws Exception {
        //given
        String invalidToken = "invalidToken";

        //when & then
        mockMvc.perform(get("/user/mypage")
                        .header("Authorization", "Bearer " + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴 시 성공한다.")
    void deleteUser_Success() throws Exception {
        //given & when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/user/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reasons\": [\"NOT_USED_OFTEN\", \"MISSING_FEATURES\"], \"userInputReason\": \"서비스가 복잡해요\"}")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴 시 토큰이 없으면 예외가 발생한다.")
    void deleteUser_NoTokenException() throws Exception {
        //given & when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/user/delete"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("회원 탈퇴 시 유효하지 않은 토큰이면 예외가 발생한다")
    void deleteUser_InvalidTokenException() throws Exception {
        //given
        String invalidToken = "invalidToken";

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/user/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reasons\": [\"NOT_USED_OFTEN\", \"MISSING_FEATURES\"], \"userInputReason\": \"서비스가 복잡해요\"}")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}
