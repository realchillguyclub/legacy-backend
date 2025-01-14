package server.poptato.todo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import server.poptato.global.response.BaseResponse;
import server.poptato.todo.api.request.*;
import server.poptato.todo.application.TodoScheduler;
import server.poptato.todo.application.TodoService;
import server.poptato.todo.application.response.HistoryCalendarListResponseDto;
import server.poptato.todo.application.response.PaginatedHistoryResponseDto;
import server.poptato.todo.application.response.TodoDetailResponseDto;
import server.poptato.user.resolver.UserId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;
    @DeleteMapping("/todo/{todoId}")
    public BaseResponse deleteTodo(@UserId Long userId, @PathVariable Long todoId) {
        todoService.deleteTodoById(userId, todoId);
        return new BaseResponse<>();
    }

    @PatchMapping("/swipe")
    public BaseResponse swipe(@UserId Long userId,
                              @Validated @RequestBody SwipeRequestDto swipeRequestDto) {
        todoService.swipe(userId, swipeRequestDto);
        return new BaseResponse<>();
    }

    @PatchMapping("/todo/{todoId}/bookmark")
    public BaseResponse toggleIsBookmark(@UserId Long userId, @PathVariable Long todoId) {
        todoService.toggleIsBookmark(userId, todoId);
        return new BaseResponse<>();
    }

    @PatchMapping("/todo/dragAndDrop")
    public BaseResponse dragAndDrop(@UserId Long userId,
                                    @Validated @RequestBody TodoDragAndDropRequestDto todoDragAndDropRequestDto) {
        todoService.dragAndDrop(userId, todoDragAndDropRequestDto);
        return new BaseResponse<>();
    }


    @GetMapping("/todo/{todoId}")
    public BaseResponse<TodoDetailResponseDto> getTodoInfo(@UserId Long userId,
                                                           @PathVariable Long todoId) {
        TodoDetailResponseDto response = todoService.getTodoInfo(userId, todoId);
        return new BaseResponse<>(response);
    }

    @PatchMapping("/todo/{todoId}/deadline")
    public BaseResponse updateDeadline(@UserId Long userId,
                                       @PathVariable Long todoId,
                                       @Validated @RequestBody DeadlineUpdateRequestDto deadlineUpdateRequestDto) {
        todoService.updateDeadline(userId, todoId, deadlineUpdateRequestDto);
        return new BaseResponse<>();
    }

    @PatchMapping("/todo/{todoId}/content")
    public BaseResponse updateContent(@UserId Long userId,
                                      @PathVariable Long todoId,
                                      @Validated @RequestBody ContentUpdateRequestDto contentUpdateRequestDto) {
        todoService.updateContent(userId, todoId, contentUpdateRequestDto);
        return new BaseResponse<>();
    }

    @PatchMapping("/todo/{todoId}/achieve")
    public BaseResponse updateIsCompleted(@UserId Long userId,
                                          @PathVariable Long todoId) {
        todoService.updateIsCompleted(userId, todoId, LocalDateTime.now());
        return new BaseResponse<>();
    }

    @PatchMapping("/todo/{todoId}/category")
    public BaseResponse updateCategory(@UserId Long userId,
                                       @PathVariable Long todoId,
                                       @RequestBody TodoCategoryUpdateRequestDto todoCategoryUpdateRequestDto) {
        todoService.updateCategory(userId, todoId, todoCategoryUpdateRequestDto);
        return new BaseResponse<>();
    }

    @PatchMapping("/todo/{todoId}/repeat")
    public BaseResponse updateIsRepeat(@UserId Long userId,
                                       @PathVariable Long todoId) {
        todoService.updateRepeat(userId, todoId);
        return new BaseResponse<>();
    }

    @GetMapping("/histories")
    public BaseResponse<PaginatedHistoryResponseDto> getHistories(
            @UserId Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam LocalDate date) {
        PaginatedHistoryResponseDto response = todoService.getHistories(userId, date, page, size);
        return new BaseResponse<>(response);
    }

    @GetMapping("/calendar")
    public BaseResponse<HistoryCalendarListResponseDto> getHistoryCalendarDateList(
            @UserId Long userId,
            @RequestParam String year,
            @RequestParam int month
    ) {
        HistoryCalendarListResponseDto response = todoService.getHistoriesCalendar(userId, year, month);
        return new BaseResponse<>(response);
    }
}
