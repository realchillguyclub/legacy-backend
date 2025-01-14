package server.poptato.auth.api;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.auth.application.service.AuthService;
import server.poptato.auth.application.service.JwtService;
import server.poptato.global.dto.TokenPair;
import server.poptato.user.application.service.UserService;
import server.poptato.user.domain.value.MobileType;
import server.poptato.user.domain.value.SocialType;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;
    @MockBean
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private TokenPair tokenPair;
    private Validator validator;
    private String accessToken;
    private String refreshToken;
    private final String userId = "1";

    @Container
    private static final GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:latest")
                    .withExposedPorts(6379)
                    .waitingFor(Wait.forListeningPort())
                    .withCreateContainerCmdModifier(cmd ->
                            cmd.withHostConfig(new HostConfig().withPortBindings(
                                    new PortBinding(Ports.Binding.bindPort(63799), new ExposedPort(6379))
                            ))
                    );

    @DynamicPropertySource
    static void configureRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeEach
    void createAccessToken_UserIdIsOne() {
        tokenPair = jwtService.generateTokenPair(userId);
        accessToken = tokenPair.accessToken();
        refreshToken = tokenPair.refreshToken();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    void deleteRefreshToken() {
        jwtService.deleteRefreshToken(userId);
    }

    @DisplayName("로그인 시, 액세스토큰이 비어있으면 Validator가 예외를 발생한다.")
    @Test
    public void login_ValidationException() {
        //given
        String authAccessToken = " ";
        SocialType socialType = SocialType.KAKAO;
        MobileType mobileType = MobileType.ANDROID;
        String clientId = "clientId";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .socialType(socialType)
                .accessToken(authAccessToken)
                .mobileType(mobileType)
                .clientId(clientId)
                .build();

        //when
        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);

        //then
        Assertions.assertEquals(violations.size(), 1);
    }

    @DisplayName("로그아웃 시, 성공한다.")
    @Test
    public void logout_Success() throws Exception {
        //when & then
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("로그아웃 시, JWT 토큰이 없으면 예외가 발생한다.")
    @Test
    public void logout_UnAuthorizedException() throws Exception {
        //when & then
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @DisplayName("토큰 재발급 요청 시, 성공한다.")
    @Test
    public void refresh_Success() throws Exception {
        //when & then
        mockMvc.perform(post("/auth/refresh")
                        .content("{\"accessToken\": \"" + accessToken + "\", \"refreshToken\": \"" + refreshToken + "\"}")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


}
