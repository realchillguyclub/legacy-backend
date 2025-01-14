package server.poptato.todo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import server.poptato.global.response.BaseResponse;
import server.poptato.todo.api.request.BacklogCreateRequestDto;
import server.poptato.todo.application.TodoBacklogService;
import server.poptato.todo.application.response.*;
import server.poptato.user.resolver.UserId;

import java.time.YearMonth;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class TodoBacklogController {
    private final TodoBacklogService todoBacklogService;

    @GetMapping("/backlogs")
    public BaseResponse<BacklogListResponseDto> getBacklogList(
            @UserId Long userId,
            @RequestParam(value = "category") Long categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size) {
        BacklogListResponseDto response = todoBacklogService.getBacklogList(userId, categoryId ,page, size);
        return new BaseResponse<>(response);
    }

    @PostMapping("/backlog")
    public BaseResponse<BacklogCreateResponseDto> generateBacklog(@UserId Long userId,
                                                                  @Validated @RequestBody BacklogCreateRequestDto backlogCreateRequestDto) {
        BacklogCreateResponseDto response = todoBacklogService.generateBacklog(userId, backlogCreateRequestDto);
        return new BaseResponse<>(response);
    }

    @GetMapping("/yesterdays")
    public BaseResponse<PaginatedYesterdayResponseDto> getYesterdays(
            @UserId Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size) {
        PaginatedYesterdayResponseDto response = todoBacklogService.getYesterdays(userId, page, size);
        return new BaseResponse<>(response);
    }
}
