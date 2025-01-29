package server.poptato.category.api;

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
import server.poptato.category.api.request.CategoryCreateUpdateRequestDto;
import server.poptato.category.api.request.CategoryDragAndDropRequestDto;
import server.poptato.category.application.CategoryService;
import server.poptato.category.application.response.CategoryCreateResponseDto;
import server.poptato.category.application.response.CategoryListResponseDto;
import server.poptato.category.application.response.CategoryResponseDto;
import server.poptato.configuration.ControllerTestConfig;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest extends ControllerTestConfig {

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("카테고리를 생성한다.")
    public void createCategory() throws Exception {
        // given
        CategoryCreateResponseDto response = CategoryCreateResponseDto.of(1L);
        Mockito.when(jwtService.extractUserIdFromToken("Bearer sampleToken"))
                .thenReturn(1L);
        Mockito.when(categoryService.createCategory(any(Long.class), any(CategoryCreateUpdateRequestDto.class)))
                .thenReturn(response);

        CategoryCreateUpdateRequestDto request = new CategoryCreateUpdateRequestDto("Work", 101L);
        String requestContent = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.post("/category")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer sampleToken")
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
                .andExpect(jsonPath("$.result.categoryId").value(1L))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("category/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Category API")
                                        .description("카테고리를 생성한다.")
                                        .requestFields(
                                                fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                                fieldWithPath("emojiId").type(JsonFieldType.NUMBER).description("이모지 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("result.categoryId").type(JsonFieldType.NUMBER).description("생성된 카테고리 ID")
                                        )
                                        .requestSchema(Schema.schema("CategoryCreateRequest"))
                                        .responseSchema(Schema.schema("CategoryCreateResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("카테고리 목록을 조회한다.")
    public void getCategories() throws Exception {
        // given
        CategoryResponseDto category1 = new CategoryResponseDto(1L, "Work", 101L, "http://emoji.url/1");
        CategoryResponseDto category2 = new CategoryResponseDto(2L, "Personal", 102L, "http://emoji.url/2");
        CategoryListResponseDto response = new CategoryListResponseDto(List.of(category1, category2), 1);

        Mockito.when(jwtService.extractUserIdFromToken("Bearer sampleToken"))
                .thenReturn(1L);
        Mockito.when(categoryService.getCategories(any(Long.class), anyInt(), anyInt())).thenReturn(response);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/category/list")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer sampleToken")
                        .param("page", "0")
                        .param("size", "6")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."))
                .andExpect(jsonPath("$.result.categories[0].id").value(1L))
                .andExpect(jsonPath("$.result.categories[0].name").value("Work"))
                .andExpect(jsonPath("$.result.categories[1].id").value(2L))
                .andExpect(jsonPath("$.result.totalPageCount").value(1))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("category/list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Category API")
                                        .description("카테고리 목록을 조회한다.")
                                        .queryParameters(
                                                parameterWithName("page").description("페이지 번호"),
                                                parameterWithName("size").description("페이지 크기")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("result.categories[].id").type(JsonFieldType.NUMBER).description("카테고리 ID"),
                                                fieldWithPath("result.categories[].name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                                fieldWithPath("result.categories[].emojiId").type(JsonFieldType.NUMBER).description("이모지 ID"),
                                                fieldWithPath("result.categories[].imageUrl").type(JsonFieldType.STRING).description("이모지 이미지 URL"),
                                                fieldWithPath("result.totalPageCount").type(JsonFieldType.NUMBER).description("총 페이지 수")
                                        )
                                        .responseSchema(Schema.schema("CategoryListResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("카테고리를 수정한다.")
    public void updateCategory() throws Exception {
        // given
        Mockito.doNothing().when(categoryService).updateCategory(any(Long.class), any(Long.class), any(CategoryCreateUpdateRequestDto.class));

        CategoryCreateUpdateRequestDto request = new CategoryCreateUpdateRequestDto("Updated Work", 105L);
        String requestContent = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.put("/category/{categoryId}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer sampleToken")
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
                .andDo(MockMvcRestDocumentationWrapper.document("category/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Category API")
                                        .description("카테고리를 수정한다.")
                                        .pathParameters(
                                                parameterWithName("categoryId").description("수정할 카테고리 ID")
                                        )
                                        .requestFields(
                                                fieldWithPath("name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                                fieldWithPath("emojiId").type(JsonFieldType.NUMBER).description("이모지 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .requestSchema(Schema.schema("CategoryUpdateRequest"))
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("카테고리를 삭제한다.")
    public void deleteCategory() throws Exception {
        // given
        Mockito.doNothing().when(categoryService).deleteCategory(any(Long.class), any(Long.class));

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/category/{categoryId}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer sampleToken")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("GLOBAL-200"))
                .andExpect(jsonPath("$.message").value("요청 응답에 성공했습니다."))

                // docs
                .andDo(MockMvcRestDocumentationWrapper.document("category/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Category API")
                                        .description("카테고리를 삭제한다.")
                                        .pathParameters(
                                                parameterWithName("categoryId").description("삭제할 카테고리 ID")
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
    @DisplayName("카테고리를 드래그 앤 드롭한다.")
    public void dragAndDropCategory() throws Exception {
        // given
        Mockito.doNothing().when(categoryService).dragAndDrop(any(Long.class), any(CategoryDragAndDropRequestDto.class));

        CategoryDragAndDropRequestDto request = new CategoryDragAndDropRequestDto(List.of(1L, 2L));
        String requestContent = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.patch("/category/dragAndDrop")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer sampleToken")
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
                .andDo(MockMvcRestDocumentationWrapper.document("category/dragAndDrop",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("Category API")
                                        .description("카테고리를 드래그 앤 드롭한다.")
                                        .requestFields(
                                                fieldWithPath("categoryIds").type(JsonFieldType.ARRAY).description("순서를 변경할 카테고리 ID 리스트")
                                        )
                                        .responseFields(
                                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                                fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                                        )
                                        .requestSchema(Schema.schema("CategoryDragAndDropRequest"))
                                        .responseSchema(Schema.schema("BaseResponse"))
                                        .build()
                        )
                ));
    }
}
