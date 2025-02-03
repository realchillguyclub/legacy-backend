package server.poptato.policy.api;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import server.poptato.auth.application.service.JwtService;
import server.poptato.configuration.ControllerTestConfig;
import server.poptato.global.response.status.SuccessStatus;
import server.poptato.policy.application.PolicyService;
import server.poptato.policy.application.response.PolicyResponseDto;

import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PolicyController.class)
public class PolicyControllerTest extends ControllerTestConfig {

    @MockBean
    private PolicyService policyService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("개인정보 처리방침을 조회한다.")
    public void getPolicy() throws Exception {
        // given
        PolicyResponseDto response = PolicyResponseDto.builder()
                .id(1L)
                .content("This is the privacy policy content.")
                .createdAt(LocalDateTime.now())
                .build();

        when(policyService.getPrivacyPolicy()).thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/policy")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(SuccessStatus._OK.getReasonHttpStatus().getCode()))
                .andExpect(jsonPath("$.message").value(SuccessStatus._OK.getReasonHttpStatus().getMessage()))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.content").value("This is the privacy policy content."))
                .andExpect(jsonPath("$.result.createdAt").exists())

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("policy/get-policy",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Policy API")
                                        .description("개인정보 처리방침을 조회한다.")
                                        .responseFields(
                                                fieldWithPath("isSuccess").type("Boolean").description("성공 여부"),
                                                fieldWithPath("code").type("String").description("응답 코드"),
                                                fieldWithPath("message").type("String").description("응답 메시지"),
                                                fieldWithPath("result.id").type("Number").description("정책 ID"),
                                                fieldWithPath("result.content").type("String").description("정책 내용"),
                                                fieldWithPath("result.createdAt").type("String").description("정책 생성 날짜")
                                        )
                                        .responseSchema(Schema.schema("PolicyResponseSchema"))
                                        .build()
                        )
                ));
    }
}
