package server.poptato.todo.api;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import server.poptato.todo.api.request.*;
import server.poptato.todo.application.TodoService;
import server.poptato.todo.application.response.*;
import server.poptato.todo.domain.value.AppVersion;
import server.poptato.todo.domain.value.Type;
import server.poptato.user.domain.value.MobileType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
public class TodoControllerTest extends ControllerTestConfig {

    @MockBean
    private TodoService todoService;

    @MockBean
    private JwtService jwtService;

    private static final String token = "Bearer sampleToken";

    @Test
    @DisplayName("할 일을 삭제한다.")
    public void deleteTodo() throws Exception {
        // given
        Mockito.doNothing().when(todoService).deleteTodoById(anyLong(), anyLong());
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/todo/{todoId}", 1L)
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일을 삭제한다.")
                                        .pathParameters(
                                                parameterWithName("todoId").description("삭제할 할 일 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일 상태를 스와이프한다.")
    public void swipeTodo() throws Exception {
        // given
        SwipeRequestDto request = new SwipeRequestDto(1L);
        Mockito.doNothing().when(todoService).swipe(anyLong(), any(SwipeRequestDto.class));
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        String requestContent = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/swipe")
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/swipe",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일 상태를 스와이프한다.")
                                        .requestFields(
                                                fieldWithPath("todoId").type(JsonFieldType.NUMBER).description("스와이프할 할 일 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일 즐겨찾기 상태를 토글한다.")
    public void toggleIsBookmark() throws Exception {
        // given
        Mockito.doNothing().when(todoService).toggleIsBookmark(anyLong(), anyLong());
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/todo/{todoId}/bookmark", 1L)
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/toggle-bookmark",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일 즐겨찾기 상태를 토글한다.")
                                        .pathParameters(
                                                parameterWithName("todoId").description("즐겨찾기 상태를 변경할 할 일 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일 순서를 드래그 앤 드롭 방식으로 변경한다.")
    public void dragAndDrop() throws Exception {
        // given
        TodoDragAndDropRequestDto request = new TodoDragAndDropRequestDto(Type.TODAY, List.of(1L, 2L, 3L));
        Mockito.doNothing().when(todoService).dragAndDrop(anyLong(), any(TodoDragAndDropRequestDto.class));
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        String requestContent = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/todo/dragAndDrop")
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/drag-and-drop",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일 순서를 드래그 앤 드롭 방식으로 변경한다.")
                                        .requestFields(
                                                fieldWithPath("type").type(JsonFieldType.STRING).description("변경할 할 일의 타입 (TODAY, BACKLOG 등)"),
                                                fieldWithPath("todoIds").type(JsonFieldType.ARRAY).description("새로운 순서대로 정렬된 할 일 ID 목록")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일의 상세 정보를 조회한다.")
    public void getTodoInfo() throws Exception {
        // given
        TodoDetailResponseDto response = new TodoDetailResponseDto(
                "할 일 내용",
                LocalTime.of(12, 55),
                LocalDate.of(2025, 1, 30),
                "개발",
                "http://example.com/emoji.png",
                true,
                false,
                true,
                List.of("월", "수")
        );

        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        Mockito.when(todoService.getTodoInfo(anyLong(), any(), anyLong())).thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/todo/{todoId}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .header("X-Mobile-Type", "ANDROID")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."))
                .andExpect(jsonPath("$.result.content").value("할 일 내용"))
                .andExpect(jsonPath("$.result.deadline").value("2025-01-30"))
                .andExpect(jsonPath("$.result.categoryName").value("개발"))
                .andExpect(jsonPath("$.result.emojiImageUrl").value("http://example.com/emoji.png"))
                .andExpect(jsonPath("$.result.isBookmark").value(true))
                .andExpect(jsonPath("$.result.isRepeat").value(false))
                .andExpect(jsonPath("$.result.isRoutine").value(true))
                .andExpect(jsonPath("$.result.routineDays[0]").value("월"))
                .andExpect(jsonPath("$.result.routineDays[1]").value("수"))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("todo/get-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일의 상세 정보를 조회한다.")
                                        .pathParameters(
                                                parameterWithName("todoId").description("조회할 할 일 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("result.content").type(JsonFieldType.STRING).description("할 일 내용"),
                                                fieldWithPath("result.time").type(JsonFieldType.STRING).description("시간"),
                                                fieldWithPath("result.deadline").type(JsonFieldType.STRING).description("마감 기한"),
                                                fieldWithPath("result.categoryName").type(JsonFieldType.STRING).description("카테고리 이름"),
                                                fieldWithPath("result.emojiImageUrl").type(JsonFieldType.STRING).description("카테고리 이모지 이미지 URL"),
                                                fieldWithPath("result.isBookmark").type(JsonFieldType.BOOLEAN).description("즐겨찾기 여부"),
                                                fieldWithPath("result.isRepeat").type(JsonFieldType.BOOLEAN).description("일반 반복 설정 여부"),
                                                fieldWithPath("result.isRoutine").type(JsonFieldType.BOOLEAN).description("요일 반복 설정 여부"),
                                                fieldWithPath("result.routineDays").type(JsonFieldType.ARRAY).description("루틴 요일 목록")
                                        )
                                        .responseSchema(Schema.schema("TodoDetailResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("X-Mobile-Type 헤더가 없는 경우 기본값(ANDROID)으로 처리된다")
    void getTodoInfo_DefaultMobileType() throws Exception {
        // given
        Long todoId = 1L;
        TodoDetailResponseDto response = new TodoDetailResponseDto(
                "할 일 내용",
                LocalTime.of(12, 55),
                LocalDate.of(2025, 1, 30),
                "개발",
                "http://example.com/emoji.png",
                true,
                false,
                true,
                List.of("월", "수")
        );

        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        Mockito.when(todoService.getTodoInfo(1L, MobileType.ANDROID, todoId))
                .thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/todo/{todoId}", todoId)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."));
    }

    @Test
    @DisplayName("할 일의 시간을 업데이트한다.")
    public void updateTime() throws Exception{
        //given
        TimeUpdateRequestDto request = new TimeUpdateRequestDto(LocalTime.of(23,55));
        Mockito.doNothing().when(todoService).updateTime(anyLong(), anyLong(), any(TimeUpdateRequestDto.class));
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        String requestContent = objectMapper.writeValueAsString(request);
        //when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/todo/{todoId}/time", 1L)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("todo/update-time",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일의 시간을 업데이트한다.")
                                        .pathParameters(
                                                parameterWithName("todoId").description("시간을 변경할 할 일 ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("todoTime").type(JsonFieldType.STRING).description("시간 (HH:mm)")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일의 요일 반복 설정을 등록한다.(v1.3.0~)")
    public void createRoutine() throws Exception {
        // given
        RoutineUpdateRequestDto request = new RoutineUpdateRequestDto(List.of("월", "수", "금"));
        Mockito.doNothing().when(todoService).createRoutine(anyLong(), anyLong(), any(RoutineUpdateRequestDto.class));
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        String requestContent = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.put("/todo/{todoId}/routine", 1L)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(requestContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-201"))
                .andExpect(jsonPath("$.message").value("생성에 성공했습니다."))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("todo/create-routine",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일의 요일 반복 설정을 등록한다.(v1.3.0~)")
                                        .pathParameters(
                                                parameterWithName("todoId").description("요일 반복 설정할 할 일 ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("routineDays").type(JsonFieldType.ARRAY).description("반복 요일 목록 (예: [\"월\", \"화\"])")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일의 요일 반복 설정을 삭제한다.(v1.3.0~)")
    public void deleteRoutine() throws Exception {
        // given
        Mockito.doNothing().when(todoService).deleteRoutine(anyLong(), anyLong());
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/todo/{todoId}/routine", 1L)
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/delete-routine",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일의 요일 반복 설정을 삭제한다.(v1.3.0~)")
                                        .pathParameters(
                                                parameterWithName("todoId").description("요일 반복 설정을 삭제할 할 일 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일의 마감 기한을 업데이트한다.")
    public void updateDeadline() throws Exception {
        // given
        DeadlineUpdateRequestDto request = new DeadlineUpdateRequestDto(LocalDate.of(2099, 1, 1));
        Mockito.doNothing().when(todoService).updateDeadline(anyLong(), anyLong(), any(DeadlineUpdateRequestDto.class));
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        String requestContent = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/todo/{todoId}/deadline", 1L)
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/update-deadline",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일의 마감 기한을 업데이트한다.")
                                        .pathParameters(
                                                parameterWithName("todoId").description("마감 기한을 변경할 할 일 ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("deadline").type(JsonFieldType.STRING).description("새로운 마감 기한 (YYYY-MM-DD)")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일의 내용을 수정한다.")
    public void updateContent() throws Exception {
        // given
        ContentUpdateRequestDto request = new ContentUpdateRequestDto("새로운 할 일 내용");
        Mockito.doNothing().when(todoService).updateContent(anyLong(), anyLong(), any(ContentUpdateRequestDto.class));
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        String requestContent = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/todo/{todoId}/content", 1L)
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/update-content",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일의 내용을 수정한다.")
                                        .pathParameters(
                                                parameterWithName("todoId").description("내용을 변경할 할 일 ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("content").type(JsonFieldType.STRING).description("새로운 할 일 내용")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일의 완료 상태를 업데이트한다.")
    public void updateIsCompleted() throws Exception {
        // given
        Mockito.doNothing().when(todoService).updateIsCompleted(anyLong(), anyLong());
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/todo/{todoId}/achieve", 1L)
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/update-achieve",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일의 완료 상태를 업데이트한다.")
                                        .pathParameters(
                                                parameterWithName("todoId").description("완료 or 미완료 상태로 변경할 할 일 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일의 카테고리를 변경한다.")
    public void updateCategory() throws Exception {
        // given
        TodoCategoryUpdateRequestDto request = new TodoCategoryUpdateRequestDto(2L);
        Mockito.doNothing().when(todoService).updateCategory(anyLong(), anyLong(), any(TodoCategoryUpdateRequestDto.class));
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        String requestContent = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/todo/{todoId}/category", 1L)
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/update-category",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일의 카테고리를 변경한다.")
                                        .pathParameters(
                                                parameterWithName("todoId").description("카테고리를 변경할 할 일 ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description("새로운 카테고리 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일의 반복 설정을 업데이트한다.(~v1.2.x)")
    public void updateIsRepeat() throws Exception {
        // given
        Mockito.doNothing().when(todoService).updateIsRepeat(anyLong(), anyLong());
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/todo/{todoId}/repeat", 1L)
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/update-repeat",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일의 반복 설정을 업데이트한다.(~v1.2.x)")
                                        .pathParameters(
                                                parameterWithName("todoId").description("반복 설정을 변경할 할 일 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일의 일반 반복 설정을 등록한다.(v1.3.0~)")
    public void createIsRepeat() throws Exception {
        // given
        Mockito.doNothing().when(todoService).createIsRepeat(anyLong(), anyLong());
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.post("/todo/{todoId}/repeat", 1L)
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/create-repeat",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일의 일반 반복 설정을 등록한다.(v1.3.0~)")
                                        .pathParameters(
                                                parameterWithName("todoId").description("일반 반복 설정할 할 일 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("할 일의 일반 반복 설정을 삭제한다.(v1.3.0~)")
    public void deleteIsRepeat() throws Exception {
        // given
        Mockito.doNothing().when(todoService).deleteIsRepeat(anyLong(), anyLong());
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/todo/{todoId}/repeat", 1L)
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/delete-repeat",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("할 일의 일반 반복 설정을 삭제한다.(v1.3.0~)")
                                        .pathParameters(
                                                parameterWithName("todoId").description("일반 반복 설정을 삭제할 할 일 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("특정 날짜의 할 일 히스토리를 조회한다.")
    public void getHistories() throws Exception {
        // given
        PaginatedHistoryResponseDto response = new PaginatedHistoryResponseDto(List.of(
                new HistoryResponseDto(1L, "test", true)
        ), 2);

        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        Mockito.when(todoService.getHistories(anyLong(), any(LocalDate.class), anyInt(), anyInt()))
                .thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/histories")
                        .param("page", "0")
                        .param("size", "15")
                        .param("date", "2025-01-29")
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/get-histories",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("특정 날짜의 할 일 히스토리를 조회한다.")
                                        .queryParameters(
                                                parameterWithName("page").description("요청 페이지 번호"),
                                                parameterWithName("size").description("한 페이지당 항목 수"),
                                                parameterWithName("date").description("조회할 날짜 (YYYY-MM-DD)")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("result.histories").type(JsonFieldType.ARRAY).description("히스토리 목록"),
                                                fieldWithPath("result.histories[].todoId").type(JsonFieldType.NUMBER).description("할 일 ID"),
                                                fieldWithPath("result.histories[].content").type(JsonFieldType.STRING).description("할 일 내용"),
                                                fieldWithPath("result.histories[].isCompleted").type(JsonFieldType.BOOLEAN).description("할 일의 완료여부"),
                                                fieldWithPath("result.totalPageCount").type(JsonFieldType.NUMBER).description("전체 페이지 수")
                                        )
                                        .responseSchema(Schema.schema("PaginatedHistoryResponse"))
                                        .build()
                        )
                ));
    }

    @ParameterizedTest
    @CsvSource({
            "V2, V2",
            "V1, V1"
    })
    @DisplayName("특정 연도 및 월의 히스토리 날짜 목록을 조회한다.(V1/V2)")
    void getHistoryCalendarDateList_Versioned(AppVersion appVersion, String docId) throws Exception {
        // given
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        if (!appVersion.isLegacy()) {
            HistoryCalendarListResponseDto response = new HistoryCalendarListResponseDto(List.of(
                    new HistoryCalendarResponseDto(LocalDate.of(2025, 1, 1), -1),
                    new HistoryCalendarResponseDto(LocalDate.of(2025, 1, 15), -1)
            ));
            Mockito.when(todoService.getHistoriesCalendar(anyLong(), anyString(), anyInt()))
                    .thenReturn(response);
        } else {
            List<LocalDate> legacyDates = List.of(
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 15)
            );
            Mockito.when(todoService.getLegacyHistoriesCalendar(anyLong(), anyString(), anyInt()))
                    .thenReturn(legacyDates);
        }

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/calendar")
                        .param("year", "2025")
                        .param("month", "1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .header("X-App-Version", appVersion)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."))
                .andDo(MockMvcRestDocumentationWrapper.document(docId,
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo API")
                                        .description("특정 연도 및 월의 히스토리 날짜 목록을 조회한다.(V1/V2)")
                                        .queryParameters(
                                                parameterWithName("year").description("조회할 연도 (YYYY)"),
                                                parameterWithName("month").description("조회할 월 (1~12)")
                                        )
                                        .responseFields(
                                                !appVersion.isLegacy()
                                                        ? List.of(
                                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                        fieldWithPath("result.historyCalendarList").type(JsonFieldType.ARRAY).description("히스토리/백로그 날짜 및 개수 목록"),
                                                        fieldWithPath("result.historyCalendarList[].date").type(JsonFieldType.STRING).description("날짜 (YYYY-MM-DD)"),
                                                        fieldWithPath("result.historyCalendarList[].count").type(JsonFieldType.NUMBER).description("할 일 개수, 히스토리만 있는 경우 -1")
                                                )
                                                        : List.of(
                                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                        fieldWithPath("result.dates").type(JsonFieldType.ARRAY).description("히스토리가 있는 날짜 목록 (YYYY-MM-DD)")
                                                )
                                        )
                                        .responseSchema(Schema.schema(
                                                appVersion.isLegacy()
                                                        ? "LegacyHistoryCalendarResponse"
                                                        : "HistoryCalendarListResponse"
                                        ))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("X-App-Version 헤더가 없는 경우 기본값(1.0)으로 처리된다")
    void getHistoryCalendarDateList_DefaultVersion() throws Exception {
        // given
        List<LocalDate> legacyDates = List.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 15)
        );

        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        Mockito.when(todoService.getLegacyHistoriesCalendar(anyLong(), anyString(), anyInt()))
                .thenReturn(legacyDates);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/calendar")
                        .param("year", "2025")
                        .param("month", "1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."))
                .andExpect(jsonPath("$.result.dates").isArray());
    }

    @Test
    @DisplayName("어제 한 일을 체크한다.")
    public void checkYesterdayTodos() throws Exception {
        // given
        CheckYesterdayTodosRequestDto request = new CheckYesterdayTodosRequestDto(List.of(1L, 2L, 3L));
        Mockito.doNothing().when(todoService).checkYesterdayTodos(anyLong(), any(CheckYesterdayTodosRequestDto.class));
        Mockito.when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);

        String requestContent = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.post("/todo/check/yesterdays")
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
                .andDo(MockMvcRestDocumentationWrapper.document("todo/check-yesterdays",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Todo-Yesterday API")
                                        .description("어제 한 일을 체크한다.")
                                        .requestFields(
                                                fieldWithPath("todoIds").type(JsonFieldType.ARRAY).description("어제 한 일 중 체크된 할 일 ID 목록")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }
}
