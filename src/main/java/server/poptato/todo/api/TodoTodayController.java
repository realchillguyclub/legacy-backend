package server.poptato.todo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.poptato.global.response.BaseResponse;
import server.poptato.todo.application.TodoTodayService;
import server.poptato.todo.application.response.TodayListResponseDto;
import server.poptato.user.resolver.UserId;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class TodoTodayController {
    private final TodoTodayService todoTodayService;

    @GetMapping("/todays")
    public BaseResponse<TodayListResponseDto> getTodayList(
            @UserId Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size){
        LocalDate todayDate = LocalDate.now();
        TodayListResponseDto response = todoTodayService.getTodayList(userId, page, size, todayDate);
        return new BaseResponse<>(response);
    }
}
