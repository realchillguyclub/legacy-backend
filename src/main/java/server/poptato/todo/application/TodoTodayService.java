package server.poptato.todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.todo.application.response.TodayListResponseDto;
import server.poptato.todo.converter.TodoDtoConverter;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.user.validator.UserValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class TodoTodayService {
    private final TodoRepository todoRepository;
    private final UserValidator userValidator;

    public TodayListResponseDto getTodayList(long userId, int page, int size, LocalDate todayDate) {
        userValidator.checkIsExistUser(userId);

        List<Todo> todays = getAllTodays(userId, todayDate);
        List<Todo> todaySubList = getTodayPagination(todays, page, size);
        int totalPageCount = (int) Math.ceil((double) todays.size() / size);

        return TodoDtoConverter.toTodayListDto(todayDate, todaySubList, totalPageCount);
    }

    private List<Todo> getTodayPagination(List<Todo> todays, int page, int size) {
        List<Todo> todaySubList;

        int start = (page) * size;
        int end = Math.min(start + size, todays.size());

        if (isThereNoToday(start, end)) return new ArrayList<>();

        todaySubList = todays.subList(start, end);
        return todaySubList;
    }

    private List<Todo> getAllTodays(long userId, LocalDate todayDate) {
        List<Todo> todays = new ArrayList<>();

        List<Todo> incompleteTodos = todoRepository.findIncompleteTodays(userId, Type.TODAY, todayDate, TodayStatus.INCOMPLETE);
        List<Todo> completedTodos = todoRepository.findCompletedTodays(userId, todayDate);

        todays.addAll(incompleteTodos);
        todays.addAll(completedTodos);

        return todays;
    }

    private boolean isThereNoToday(int start, int end) {
        return start >= end;
    }
}
