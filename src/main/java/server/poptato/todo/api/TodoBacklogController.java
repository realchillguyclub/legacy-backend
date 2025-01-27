package server.poptato.todo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import server.poptato.todo.api.request.BacklogCreateRequestDto;
import server.poptato.todo.application.TodoBacklogService;
import server.poptato.todo.application.response.*;
import server.poptato.global.response.ApiResponse;
import server.poptato.global.response.status.SuccessStatus;
import server.poptato.user.resolver.UserId;

@RestController
@RequiredArgsConstructor
public class TodoBacklogController {
    private final TodoBacklogService todoBacklogService;

    /**
     * 백로그 목록 조회 API.
     *
     * 사용자가 선택한 카테고리에 해당하는 백로그 목록을 조회합니다.
     * 요청 파라미터로 페이지 번호와 크기를 전달하여 페이징된 데이터를 가져옵니다.
     *
     * @param userId 사용자 ID (자동 주입)
     * @param categoryId 조회할 카테고리 ID
     * @param page 요청 페이지 번호 (기본값: 0)
     * @param size 한 페이지당 항목 수 (기본값: 8)
     * @return 백로그 목록 및 페이징 정보
     */
    @GetMapping("/backlogs")
    public ResponseEntity<ApiResponse<BacklogListResponseDto>> getBacklogList(
            @UserId Long userId,
            @RequestParam(value = "category") Long categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size) {
        BacklogListResponseDto response = todoBacklogService.getBacklogList(userId, categoryId, page, size);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    /**
     * 백로그 생성 API.
     *
     * 사용자가 새로운 백로그를 생성합니다. 요청 본문으로 백로그의 제목과 세부 내용을 전달받습니다.
     *
     * @param userId 사용자 ID (자동 주입)
     * @param backlogCreateRequestDto 백로그 생성 요청 데이터
     * @return 생성된 백로그의 ID 및 기본 정보
     */
    @PostMapping("/backlog")
    public ResponseEntity<ApiResponse<BacklogCreateResponseDto>> generateBacklog(
            @UserId Long userId,
            @Validated @RequestBody BacklogCreateRequestDto backlogCreateRequestDto) {
        BacklogCreateResponseDto response = todoBacklogService.generateBacklog(userId, backlogCreateRequestDto);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    /**
     * 어제의 백로그 목록 조회 API.
     *
     * 사용자의 어제 작업했던 백로그 항목들을 조회합니다.
     * 요청 파라미터로 페이지 번호와 크기를 전달하여 페이징된 데이터를 가져옵니다.
     *
     * @param userId 사용자 ID (자동 주입)
     * @param page 요청 페이지 번호 (기본값: 0)
     * @param size 한 페이지당 항목 수 (기본값: 15)
     * @return 어제의 백로그 목록 및 페이징 정보
     */
    @GetMapping("/yesterdays")
    public ResponseEntity<ApiResponse<PaginatedYesterdayResponseDto>> getYesterdays(
            @UserId Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size) {
        PaginatedYesterdayResponseDto response = todoBacklogService.getYesterdays(userId, page, size);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}
