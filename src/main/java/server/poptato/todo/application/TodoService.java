package server.poptato.todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.category.domain.entity.Category;
import server.poptato.category.domain.repository.CategoryRepository;
import server.poptato.category.exception.CategoryException;
import server.poptato.category.validator.CategoryValidator;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.emoji.domain.repository.EmojiRepository;
import server.poptato.emoji.exception.EmojiException;
import server.poptato.todo.api.request.*;
import server.poptato.todo.application.response.HistoryCalendarListResponseDto;
import server.poptato.todo.application.response.PaginatedHistoryResponseDto;
import server.poptato.todo.application.response.TodoDetailResponseDto;
import server.poptato.todo.converter.TodoDtoConverter;
import server.poptato.todo.domain.entity.CompletedDateTime;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.CompletedDateTimeRepository;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.todo.exception.TodoException;
import server.poptato.todo.exception.errorcode.TodoExceptionErrorCode;
import server.poptato.user.validator.UserValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.query.sqm.tree.SqmNode.log;
import static server.poptato.category.exception.errorcode.CategoryExceptionErrorCode.CATEGORY_NOT_EXIST;
import static server.poptato.emoji.exception.errorcode.EmojiExceptionErrorCode.EMOJI_NOT_EXIST;
import static server.poptato.todo.exception.errorcode.TodoExceptionErrorCode.COMPLETED_DATETIME_NOT_EXIST;
import static server.poptato.todo.exception.errorcode.TodoExceptionErrorCode.TODO_NOT_EXIST;

@Transactional
@RequiredArgsConstructor
@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final CompletedDateTimeRepository completedDateTimeRepository;
    private final UserValidator userValidator;
    private final CategoryValidator categoryValidator;
    private final CategoryRepository categoryRepository;
    private final EmojiRepository emojiRepository;


    public void deleteTodoById(Long userId, Long todoId) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        todoRepository.delete(findTodo);
    }

    private Todo validateAndReturnTodo(Long userId, Long todoId) {
        Todo findTodo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoException(TODO_NOT_EXIST));
        if (findTodo.getUserId() != userId)
            throw new TodoException(TodoExceptionErrorCode.TODO_USER_NOT_MATCH);
        return findTodo;
    }

    public void toggleIsBookmark(Long userId, Long todoId) {
        Todo todo = validateAndReturnTodo(userId, todoId);
        todo.toggleBookmark();
    }


    public void swipe(Long userId, SwipeRequestDto swipeRequestDto) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, swipeRequestDto.getTodoId());
        if (isToday(findTodo)) {
            swipeTodayToBacklog(findTodo);
            return;
        }
        swipeBacklogToToday(findTodo);
    }

    private boolean isToday(Todo findTodo) {
        return findTodo.getType().equals(Type.TODAY);
    }

    private void swipeBacklogToToday(Todo todo) {
        Integer maxTodayOrder = todoRepository.findMaxTodayOrderByUserIdOrZero(todo.getUserId());
        todo.changeToToday(maxTodayOrder);
    }

    private void swipeTodayToBacklog(Todo todo) {
        if (isCompletedTodo(todo))
            throw new TodoException(TodoExceptionErrorCode.ALREADY_COMPLETED_TODO);
        Integer maxBacklogOrder = todoRepository.findMaxBacklogOrderByUserIdOrZero(todo.getUserId());
        todo.changeToBacklog(maxBacklogOrder);
    }

    private boolean isCompletedTodo(Todo todo) {
        return todo.getTodayStatus().equals(TodayStatus.COMPLETED);
    }


    public void dragAndDrop(Long userId, TodoDragAndDropRequestDto requestDto) {
        userValidator.checkIsExistUser(userId);
        List<Todo> todos = getTodosByIds(requestDto.getTodoIds());
        checkIsValidToDragAndDrop(userId, todos, requestDto);
        if (isTypeToday(requestDto.getType())) {
            reassignTodayOrder(todos);
            return;
        }
        reassignBacklogOrder(todos);
    }

    private List<Todo> getTodosByIds(List<Long> todoIds) {
        List<Todo> todos = new ArrayList<>();
        for (Long todoId : todoIds) {
            todos.add(todoRepository.findById(todoId).get());
        }
        return todos;
    }

    private void checkIsValidToDragAndDrop(Long userId, List<Todo> todos, TodoDragAndDropRequestDto todoDragAndDropRequestDto) {
        if (todos.size() != todoDragAndDropRequestDto.getTodoIds().size()) {
            throw new TodoException(TodoExceptionErrorCode.TODO_NOT_EXIST);
        }
        for (Todo todo : todos) {
            if (!todo.getUserId().equals(userId)) {
                throw new TodoException(TodoExceptionErrorCode.TODO_USER_NOT_MATCH);
            }
            if (todoDragAndDropRequestDto.getType().equals(Type.TODAY) && todo.getTodayStatus() == TodayStatus.COMPLETED) {
                throw new TodoException(TodoExceptionErrorCode.ALREADY_COMPLETED_TODO);
            }
            if (todoDragAndDropRequestDto.getType().equals(Type.TODAY)) {
                if (!todo.getType().equals(Type.TODAY)) {
                    throw new TodoException(TodoExceptionErrorCode.TODO_TYPE_NOT_MATCH);
                }
            }
            if (todoDragAndDropRequestDto.getType().equals(Type.BACKLOG)) {
                if (!(todo.getType().equals(Type.BACKLOG) || todo.getType().equals(Type.YESTERDAY))) {
                    throw new TodoException(TodoExceptionErrorCode.TODO_TYPE_NOT_MATCH);
                }
            }
        }
    }

    private List<Integer> getTodayOrders(List<Todo> todos) {
        List<Integer> todayOrders = new ArrayList<>();
        for (Todo todo : todos) {
            todayOrders.add(todo.getTodayOrder());
        }
        return todayOrders;
    }

    private void reassignTodayOrder(List<Todo> todos) {
        List<Integer> todayOrders = getTodayOrders(todos);
        Collections.sort(todayOrders, Collections.reverseOrder());
        for (int i = 0; i < todos.size(); i++) {
            todos.get(i).setTodayOrder(todayOrders.get(i));
            todoRepository.save(todos.get(i));
        }
    }

    private List<Integer> getBacklogOrders(List<Todo> todos) {
        List<Integer> backlogOrders = new ArrayList<>();
        for (Todo todo : todos) {
            backlogOrders.add(todo.getBacklogOrder());
        }
        return backlogOrders;
    }

    private void reassignBacklogOrder(List<Todo> todos) {
        List<Integer> backlogOrders = getBacklogOrders(todos);
        Collections.sort(backlogOrders, Collections.reverseOrder());
        for (int i = 0; i < todos.size(); i++) {
            todos.get(i).setBacklogOrder(backlogOrders.get(i));
            todoRepository.save(todos.get(i));
        }
    }

    private boolean isTypeToday(Type type) {
        return type.equals(Type.TODAY);
    }

    public TodoDetailResponseDto getTodoInfo(Long userId, Long todoId) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        Category findCategory = findTodo.getCategoryId() != null ?
                categoryRepository.findById(findTodo.getCategoryId()).orElse(null) : null;

        Emoji findEmoji = findCategory != null && findCategory.getEmojiId() != null ?
                emojiRepository.findById(findCategory.getEmojiId()).orElse(null) : null;
        return TodoDtoConverter.toTodoDetailInfoDto(findTodo, findCategory, findEmoji);
    }

    public void updateDeadline(Long userId, Long todoId, DeadlineUpdateRequestDto deadlineUpdateRequestDto) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        findTodo.updateDeadline(deadlineUpdateRequestDto.getDeadline());
        todoRepository.save(findTodo);
    }

    public void updateContent(Long userId, Long todoId, ContentUpdateRequestDto contentUpdateRequestDto) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        findTodo.updateContent(contentUpdateRequestDto.getContent());
        todoRepository.save(findTodo);
    }

    public void updateIsCompleted(Long userId, Long todoId, LocalDateTime now) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        checkIsValidToUpdateIsCompleted(findTodo);
        if (isTypeYesterday(findTodo.getType())) {
            updateYesterdayIsCompleted(findTodo);
            todoRepository.save(findTodo);
            return;
        }
        if (isTypeToday(findTodo.getType())) updateTodayIsCompleted(findTodo, now);
        todoRepository.save(findTodo);
    }

    private void updateYesterdayIsCompleted(Todo findTodo) {
        if (TodayStatus.INCOMPLETE.equals(findTodo.getTodayStatus())) {
            LocalDateTime yesterday = LocalDateTime.of(findTodo.getTodayDate(), LocalTime.of(23, 59));
            findTodo.updateYesterdayToCompleted();
            CompletedDateTime completedDateTime = CompletedDateTime.builder().todoId(findTodo.getId()).dateTime(yesterday).build();
            completedDateTimeRepository.save(completedDateTime);
            return;
        }
        if (TodayStatus.COMPLETED.equals(findTodo.getTodayStatus())) {
            Integer minBacklogOrder = todoRepository.findMinBacklogOrderByUserIdOrZero(findTodo.getUserId());
            findTodo.updateYesterdayToInComplete(minBacklogOrder);
            CompletedDateTime completedDateTime = completedDateTimeRepository.findByDateAndTodoId(findTodo.getId(), findTodo.getTodayDate())
                    .orElseThrow(() -> new TodoException(COMPLETED_DATETIME_NOT_EXIST));
            completedDateTimeRepository.delete(completedDateTime);
        }
    }

    private void updateTodayIsCompleted(Todo findTodo, LocalDateTime now) {
        if (TodayStatus.INCOMPLETE.equals(findTodo.getTodayStatus())) {
            findTodo.updateTodayToCompleted();
            CompletedDateTime completedDateTime = CompletedDateTime.builder().todoId(findTodo.getId()).dateTime(now).build();
            completedDateTimeRepository.save(completedDateTime);
            return;
        }
        if (TodayStatus.COMPLETED.equals(findTodo.getTodayStatus())) {
            Integer minTodayOrder = todoRepository.findMinTodayOrderByUserIdOrZero(findTodo.getUserId());
            findTodo.updateTodayToInComplete(minTodayOrder);
            CompletedDateTime completedDateTime = completedDateTimeRepository.findByDateAndTodoId(findTodo.getId(), findTodo.getTodayDate())
                    .orElseThrow(() -> new TodoException(COMPLETED_DATETIME_NOT_EXIST));
            completedDateTimeRepository.delete(completedDateTime);
        }
    }

    private void checkIsValidToUpdateIsCompleted(Todo todo) {
        if (todo.getType().equals(Type.BACKLOG))
            throw new TodoException(TodoExceptionErrorCode.BACKLOG_CANT_COMPLETE);
    }

    private boolean isTypeYesterday(Type type) {
        return type.equals(Type.YESTERDAY);
    }

    public PaginatedHistoryResponseDto getHistories(Long userId, LocalDate localDate, int page, int size) {
        userValidator.checkIsExistUser(userId);
        Page<Todo> historiesPage = getHistoriesPage(userId, localDate, page, size);
        return TodoDtoConverter.toHistoryListDto(historiesPage);
    }

    private Page<Todo> getHistoriesPage(Long userId, LocalDate localDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Todo> historiesPage = todoRepository.findHistories(userId, localDate, pageable);
        return historiesPage;
    }

    public HistoryCalendarListResponseDto getHistoriesCalendar(Long userId, String year, int month) {
        List<LocalDateTime> dateTimes = completedDateTimeRepository.findHistoryExistingDates(userId, year, month);
        List<LocalDate> dates = dateTimes.stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .toList();
        return HistoryCalendarListResponseDto.builder().dates(dates).build();
    }

    public void updateCategory(Long userId, Long todoId, TodoCategoryUpdateRequestDto requestDto) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        if (requestDto.categoryId() != null) categoryValidator.validateCategory(userId, requestDto.categoryId());
        findTodo.updateCategory(requestDto.categoryId());
        todoRepository.save(findTodo);
    }

    public void updateRepeat(Long userId, Long todoId) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        findTodo.updateIsRepeat();
        todoRepository.save(findTodo);
    }
}
