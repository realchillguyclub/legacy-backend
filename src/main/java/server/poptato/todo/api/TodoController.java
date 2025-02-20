package server.poptato.todo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import server.poptato.auth.application.service.JwtService;
import server.poptato.todo.api.request.*;
import server.poptato.todo.application.TodoService;
import server.poptato.todo.application.response.HistoryCalendarListResponseDto;
import server.poptato.todo.application.response.PaginatedHistoryResponseDto;
import server.poptato.todo.application.response.TodoDetailResponseDto;
import server.poptato.global.response.ApiResponse;
import server.poptato.global.response.status.SuccessStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final JwtService jwtService;

    /**
     * 할 일 삭제 API.
     *
     * 사용자가 특정 할 일을 삭제합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param todoId 삭제할 할 일 ID
     * @return 성공 여부를 나타내는 응답
     */
    @DeleteMapping("/todo/{todoId}")
    public ResponseEntity<ApiResponse<SuccessStatus>> deleteTodo(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long todoId
    ) {
        todoService.deleteTodoById(jwtService.extractUserIdFromToken(authorizationHeader), todoId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 할 일 상태 스와이프 API.
     *
     * 사용자가 할 일 상태를 스와이프 방식으로 업데이트합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param swipeRequestDto 스와이프 요청 데이터
     * @return 성공 여부를 나타내는 응답
     */
    @PatchMapping("/swipe")
    public ResponseEntity<ApiResponse<SuccessStatus>> swipe(
            @RequestHeader("Authorization") String authorizationHeader,
            @Validated @RequestBody SwipeRequestDto swipeRequestDto
    ) {
        todoService.swipe(jwtService.extractUserIdFromToken(authorizationHeader), swipeRequestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 할 일 즐겨찾기 상태 토글 API.
     *
     * 사용자가 특정 할 일의 즐겨찾기 상태를 토글합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param todoId 대상 할 일 ID
     * @return 성공 여부를 나타내는 응답
     */
    @PatchMapping("/todo/{todoId}/bookmark")
    public ResponseEntity<ApiResponse<SuccessStatus>> toggleIsBookmark(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long todoId
    ) {
        todoService.toggleIsBookmark(jwtService.extractUserIdFromToken(authorizationHeader), todoId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 할 일 드래그 앤 드롭 API.
     *
     * 사용자가 드래그 앤 드롭 방식으로 할 일 순서를 변경합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param todoDragAndDropRequestDto 순서 변경 요청 데이터
     * @return 성공 여부를 나타내는 응답
     */
    @PatchMapping("/todo/dragAndDrop")
    public ResponseEntity<ApiResponse<SuccessStatus>> dragAndDrop(
            @RequestHeader("Authorization") String authorizationHeader,
            @Validated @RequestBody TodoDragAndDropRequestDto todoDragAndDropRequestDto
    ) {
        todoService.dragAndDrop(jwtService.extractUserIdFromToken(authorizationHeader), todoDragAndDropRequestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 할 일 상세 조회 API.
     *
     * 특정 할 일의 세부 정보를 조회합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param todoId 조회할 할 일 ID
     * @return 할 일 상세 정보
     */
    @GetMapping("/todo/{todoId}")
    public ResponseEntity<ApiResponse<TodoDetailResponseDto>> getTodoInfo(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long todoId
    ) {
        TodoDetailResponseDto response = todoService.getTodoInfo(jwtService.extractUserIdFromToken(authorizationHeader), todoId);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    /**
     * 할 일 기한 업데이트 API.
     *
     * 사용자가 특정 할 일의 마감 기한을 업데이트합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param todoId 업데이트할 할 일 ID
     * @param deadlineUpdateRequestDto 마감 기한 업데이트 요청 데이터
     * @return 성공 여부를 나타내는 응답
     */
    @PatchMapping("/todo/{todoId}/deadline")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateDeadline(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long todoId,
            @Validated @RequestBody DeadlineUpdateRequestDto deadlineUpdateRequestDto
    ) {
        todoService.updateDeadline(jwtService.extractUserIdFromToken(authorizationHeader), todoId, deadlineUpdateRequestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 할 일 내용 수정 API.
     *
     * 사용자가 특정 할 일의 내용을 수정합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param todoId 수정할 할 일 ID
     * @param contentUpdateRequestDto 내용 수정 요청 데이터
     * @return 성공 여부를 나타내는 응답
     */
    @PatchMapping("/todo/{todoId}/content")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateContent(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long todoId,
            @Validated @RequestBody ContentUpdateRequestDto contentUpdateRequestDto
    ) {
        todoService.updateContent(jwtService.extractUserIdFromToken(authorizationHeader), todoId, contentUpdateRequestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 할 일 완료 상태 업데이트 API.
     *
     * 사용자가 특정 할 일의 완료 상태를 업데이트합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param todoId 업데이트할 할 일 ID
     * @return 성공 여부를 나타내는 응답
     */
    @PatchMapping("/todo/{todoId}/achieve")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateIsCompleted(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long todoId
    ) {
        todoService.updateIsCompleted(jwtService.extractUserIdFromToken(authorizationHeader), todoId, LocalDateTime.now());
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 할 일 카테고리 변경 API.
     *
     * 사용자가 특정 할 일의 카테고리를 변경합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param todoId 변경할 할 일 ID
     * @param todoCategoryUpdateRequestDto 카테고리 변경 요청 데이터
     * @return 성공 여부를 나타내는 응답
     */
    @PatchMapping("/todo/{todoId}/category")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateCategory(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long todoId,
            @RequestBody TodoCategoryUpdateRequestDto todoCategoryUpdateRequestDto
    ) {
        todoService.updateCategory(jwtService.extractUserIdFromToken(authorizationHeader), todoId, todoCategoryUpdateRequestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 반복 설정 업데이트 API.
     *
     * 사용자가 특정 할 일의 반복 설정을 업데이트합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param todoId 업데이트할 할 일 ID
     * @return 성공 여부를 나타내는 응답
     */
    @PatchMapping("/todo/{todoId}/repeat")
    public ResponseEntity<ApiResponse<SuccessStatus>> updateIsRepeat(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long todoId
    ) {
        todoService.updateRepeat(jwtService.extractUserIdFromToken(authorizationHeader), todoId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    /**
     * 히스토리 조회 API.
     *
     * 사용자가 특정 날짜의 할 일 히스토리를 조회합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param page 요청 페이지 번호 (기본값: 0)
     * @param size 한 페이지당 항목 수 (기본값: 15)
     * @param date 조회할 날짜
     * @return 히스토리 목록
     */
    @GetMapping("/histories")
    public ResponseEntity<ApiResponse<PaginatedHistoryResponseDto>> getHistories(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam LocalDate date
    ) {
        PaginatedHistoryResponseDto response = todoService.getHistories(jwtService.extractUserIdFromToken(authorizationHeader), date, page, size);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    /**
     * 히스토리 캘린더 조회 API.
     *
     * 사용자가 특정 연도 및 월의 할 일 히스토리를 조회합니다.
     *
     * @param authorizationHeader 요청 헤더의 Authorization (Bearer 토큰)
     * @param year 조회할 연도
     * @param month 조회할 월
     * @return 해당 연도와 월의 할 일 히스토리 날짜 목록
     */
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<HistoryCalendarListResponseDto>> getHistoryCalendarDateList(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String year,
            @RequestParam int month
    ) {
        HistoryCalendarListResponseDto response = todoService.getHistoriesCalendar(jwtService.extractUserIdFromToken(authorizationHeader), year, month);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}
