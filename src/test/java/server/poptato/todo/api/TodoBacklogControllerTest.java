package server.poptato.todo.api;

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
import server.poptato.todo.api.request.BacklogCreateRequestDto;
import server.poptato.todo.application.TodoBacklogService;
import server.poptato.todo.application.response.BacklogCreateResponseDto;
import server.poptato.todo.application.response.BacklogListResponseDto;
import server.poptato.todo.application.response.PaginatedYesterdayResponseDto;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoBacklogController.class)
public class TodoBacklogControllerTest extends ControllerTestConfig {

    @MockBean
    private TodoBacklogService todoBacklogService;

    @MockBean
    private JwtService jwtService;

    private static final String token = "Bearer sampleToken";

    @Test
    @DisplayName("백로그 목록을 조회한다.")
    public void getBacklogList() throws Exception {
        // given
        BacklogListResponseDto response = new BacklogListResponseDto(10L, "Sample Category", List.of(), 1);

        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        Mockito.when(todoBacklogService.getBacklogList(anyLong(), anyLong(), anyInt(), anyInt())).thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/backlogs")
                        .param("category", "1")
                        .param("page", "0")
                        .param("size", "8")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("backlog/get-backlogs",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo-Backlog API")
                                        .description("백로그 목록을 조회한다.")
                                        .queryParameters(
                                                parameterWithName("category").description("카테고리 ID"),
                                                parameterWithName("page").description("요청 페이지 번호"),
                                                parameterWithName("size").description("페이지 크기")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("result.totalCount").type(JsonFieldType.NUMBER).description("백로그 총 개수"),
                                                fieldWithPath("result.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                                                fieldWithPath("result.backlogs").type(JsonFieldType.ARRAY).description("백로그 목록"),
                                                fieldWithPath("result.totalPageCount").type(JsonFieldType.NUMBER).description("전체 페이지 수")
                                        )
                                        .responseSchema(Schema.schema("BacklogListResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("백로그를 생성한다.")
    public void createBacklog() throws Exception {
        // given
        BacklogCreateRequestDto request = new BacklogCreateRequestDto("New Backlog", 1L);
        BacklogCreateResponseDto response = new BacklogCreateResponseDto(1L);

        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        Mockito.when(todoBacklogService.generateBacklog(anyLong(), any(BacklogCreateRequestDto.class))).thenReturn(response);

        String requestContent = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.post("/backlog")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("backlog/create-backlog",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo-Backlog API")
                                        .description("백로그를 생성한다.")
                                        .requestSchema(Schema.schema("BacklogCreateRequest"))
                                        .responseSchema(Schema.schema("BacklogCreateResponse"))
                                        .requestFields(
                                                fieldWithPath("content").type(JsonFieldType.STRING).description("백로그 내용"),
                                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("카테고리 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("result.todoId").type(JsonFieldType.NUMBER).description("생성된 백로그 ID")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("어제의 미완료 할 일들을 조회한다.")
    public void getYesterdays() throws Exception {
        // given
        PaginatedYesterdayResponseDto response = new PaginatedYesterdayResponseDto(List.of(), 1);

        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        Mockito.when(todoBacklogService.getYesterdays(anyLong(), anyInt(), anyInt())).thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/yesterdays")
                        .param("page", "0")
                        .param("size", "15")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("backlog/get-yesterdays",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo-Yesterday API")
                                        .description("어제의 미완료 할 일들을 조회한다.")
                                        .queryParameters(
                                                parameterWithName("page").description("요청 페이지 번호"),
                                                parameterWithName("size").description("페이지 크기")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("result.yesterdays").type(JsonFieldType.ARRAY).description("어제의 백로그 목록"),
                                                fieldWithPath("result.totalPageCount").type(JsonFieldType.NUMBER).description("전체 페이지 수")
                                        )
                                        .responseSchema(Schema.schema("PaginatedYesterdayResponse"))
                                        .build()
                        )
                ));
    }
}
