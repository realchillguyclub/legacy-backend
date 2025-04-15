package server.poptato.emoji.api;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import server.poptato.configuration.ControllerTestConfig;
import server.poptato.emoji.api.controller.EmojiController;
import server.poptato.emoji.application.response.EmojiDto;
import server.poptato.emoji.application.response.EmojiResponseDto;
import server.poptato.emoji.application.service.EmojiService;
import server.poptato.user.domain.value.MobileType;

import java.util.List;
import java.util.Map;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmojiController.class)
public class EmojiControllerTest extends ControllerTestConfig {

    @MockBean
    private EmojiService emojiService;

    @Test
    @DisplayName("이모지 목록을 조회한다.")
    public void getEmojis() throws Exception {
        // given
        Map<String, List<EmojiDto>> groupedEmojis = Map.of(
                "Group1", List.of(new EmojiDto(1L, "url1"), new EmojiDto(2L, "url2")),
                "Group2", List.of(new EmojiDto(3L, "url3"))
        );
        EmojiResponseDto response = new EmojiResponseDto(groupedEmojis, 2);

        Mockito.when(emojiService.getGroupedEmojis(any(MobileType.class), anyInt(), anyInt())).thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/emojis")
                        .param("page", "0")
                        .param("size", "70")
                        .header("X-Mobile-Type", "ANDROID")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."))
                .andExpect(jsonPath("$.result.groupEmojis.Group1[0].emojiId").value(1))
                .andExpect(jsonPath("$.result.groupEmojis.Group1[0].imageUrl").value("url1"))
                .andExpect(jsonPath("$.result.groupEmojis.Group1[1].emojiId").value(2))
                .andExpect(jsonPath("$.result.groupEmojis.Group2[0].emojiId").value(3))
                .andExpect(jsonPath("$.result.totalPageCount").value(2))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("emojis/get-emojis",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Emoji API")
                                        .description("이모지 목록을 조회한다.")
                                        .queryParameters(
                                                parameterWithName("page").description("요청 페이지 번호").optional(),
                                                parameterWithName("size").description("페이지당 항목 수").optional()
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("result.groupEmojis").type(JsonFieldType.OBJECT).description("그룹화된 이모지 목록"),
                                                fieldWithPath("result.groupEmojis.*[].emojiId").type(JsonFieldType.NUMBER).description("이모지 ID"),
                                                fieldWithPath("result.groupEmojis.*[].imageUrl").type(JsonFieldType.STRING).description("이모지 이미지 URL"),
                                                fieldWithPath("result.totalPageCount").type(JsonFieldType.NUMBER).description("전체 페이지 수")
                                        )
                                        .responseSchema(Schema.schema("EmojiResponseSchema"))
                                        .build()
                        )
                ));
    }
}
