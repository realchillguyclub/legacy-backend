package server.poptato.todo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.poptato.todo.application.TodoTodayService;
import server.poptato.todo.application.response.TodayListResponseDto;
import server.poptato.global.response.ApiResponse;
import server.poptato.global.response.status.SuccessStatus;
import server.poptato.user.resolver.UserId;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class TodoTodayController {
    private final TodoTodayService todoTodayService;

    /**
     * 오늘의 할 일 조회 API.
     *
     * 사용자가 오늘 해야 할 일을 조회합니다. 요청 파라미터로 페이지 번호와 크기를 전달하여 페이징된 데이터를 가져옵니다.
     *
     * @param userId 사용자 ID (자동 주입)
     * @param page 요청 페이지 번호 (기본값: 0)
     * @param size 한 페이지당 항목 수 (기본값: 8)
     * @return 오늘의 할 일 목록
     */
    @GetMapping("/todays")
    public ResponseEntity<ApiResponse<TodayListResponseDto>> getTodayList(
            @UserId Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size) {
        LocalDate todayDate = LocalDate.now();
        TodayListResponseDto response = todoTodayService.getTodayList(userId, page, size, todayDate);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}
