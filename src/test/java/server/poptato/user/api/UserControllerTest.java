package server.poptato.user.api;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import server.poptato.auth.application.service.JwtService;
import server.poptato.configuration.ControllerTestConfig;
import server.poptato.global.response.status.SuccessStatus;
import server.poptato.user.api.request.UserDeleteRequestDTO;
import server.poptato.user.application.response.UserInfoResponseDto;
import server.poptato.user.application.service.UserService;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.value.Reason;
import server.poptato.user.domain.value.SocialType;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest extends ControllerTestConfig {

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private static final String token = "Bearer sampleToken";

    @Test
    @DisplayName("사용자가 탈퇴 요청을 보낸다.")
    public void deleteUser() throws Exception {
        // given
        UserDeleteRequestDTO request = new UserDeleteRequestDTO(
                List.of(Reason.NOT_USED_OFTEN),
                "Other reason"
        );

        String requestContent = objectMapper.writeValueAsString(request);
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        Mockito.doNothing().when(userService).deleteUser(any(Long.class), any(UserDeleteRequestDTO.class));

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.post("/user/delete")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getReasonHttpStatus().getCode()))
                .andExpect(jsonPath("$.message").value(SuccessStatus._OK.getReasonHttpStatus().getMessage()))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("user/delete-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User API")
                                        .description("사용자가 탈퇴 요청을 보낸다.")
                                        .requestFields(
                                                fieldWithPath("reasons[]").type(JsonFieldType.ARRAY).description("탈퇴 이유 목록"),
                                                fieldWithPath("userInputReason").type(JsonFieldType.STRING).description("직접 입력한 탈퇴 이유").optional()
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .requestSchema(Schema.schema("UserDeleteRequestSchema"))
                                        .responseSchema(Schema.schema("SuccessResponseSchema"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("사용자가 마이페이지 정보를 조회한다.")
    public void getUserInfo() throws Exception {
        // given
        Long userId = 1L;
        User user = User.builder()
                .socialType(SocialType.KAKAO)
                .socialId("123456")
                .name("John Doe")
                .email("john.doe@example.com")
                .imageUrl("http://example.com/image.jpg")
                .isPushAlarm(true)
                .build();

        UserInfoResponseDto response = UserInfoResponseDto.of(user);

        // JwtService Mock 설정
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(userId);
        Mockito.when(userService.getUserInfo(userId)).thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/user/mypage")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."))
                .andExpect(jsonPath("$.result.name").value("John Doe"))
                .andExpect(jsonPath("$.result.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.result.imageUrl").value("http://example.com/image.jpg"))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("user/get-user-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("User API")
                                        .description("사용자가 마이페이지 정보를 조회한다.")
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                                fieldWithPath("result.name").type(JsonFieldType.STRING).description("사용자 이름"),
                                                fieldWithPath("result.email").type(JsonFieldType.STRING).description("사용자 이메일"),
                                                fieldWithPath("result.imageUrl").type(JsonFieldType.STRING).description("사용자 프로필 이미지 URL")
                                        )
                                        .responseSchema(Schema.schema("UserInfoResponseSchema"))
                                        .build()
                        )
                ));
    }
}
