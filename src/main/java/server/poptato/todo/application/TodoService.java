package server.poptato.todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.category.domain.entity.Category;
import server.poptato.category.domain.repository.CategoryRepository;
import server.poptato.category.validator.CategoryValidator;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.emoji.domain.repository.EmojiRepository;
import server.poptato.global.exception.CustomException;
import server.poptato.todo.api.request.*;
import server.poptato.todo.application.response.HistoryCalendarListResponseDto;
import server.poptato.todo.application.response.PaginatedHistoryResponseDto;
import server.poptato.todo.application.response.TodoDetailResponseDto;
import server.poptato.todo.domain.entity.CompletedDateTime;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.CompletedDateTimeRepository;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.todo.status.TodoErrorStatus;
import server.poptato.user.validator.UserValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

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

    /**
     * 특정 할 일을 삭제합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 삭제할 할 일 ID
     */
    public void deleteTodoById(Long userId, Long todoId) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        todoRepository.delete(findTodo);
    }

    /**
     * 특정 할 일을 조회하고 유효성 검사를 수행합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 할 일 ID
     * @return 조회된 할 일 엔티티
     */
    private Todo validateAndReturnTodo(Long userId, Long todoId) {
        Todo findTodo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(TodoErrorStatus._TODO_NOT_EXIST));
        if (!findTodo.getUserId().equals(userId)) {
            throw new CustomException(TodoErrorStatus._TODO_USER_NOT_MATCH);
        }
        return findTodo;
    }

    /**
     * 특정 할 일의 즐겨찾기 상태를 토글합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 대상 할 일 ID
     */
    public void toggleIsBookmark(Long userId, Long todoId) {
        Todo todo = validateAndReturnTodo(userId, todoId);
        todo.toggleBookmark();
    }

    /**
     * 할 일의 상태를 스와이프 방식으로 변경합니다.
     *
     * @param userId 사용자 ID
     * @param swipeRequestDto 스와이프 요청 데이터
     */
    public void swipe(Long userId, SwipeRequestDto swipeRequestDto) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, swipeRequestDto.todoId());
        if (isToday(findTodo)) {
            swipeTodayToBacklog(findTodo);
            return;
        }
        swipeBacklogToToday(findTodo);
    }

    /**
     * 특정 할 일이 TODAY 타입인지 확인합니다.
     *
     * @param findTodo 할 일 객체
     * @return TODAY 타입이면 true, 아니면 false
     */
    private boolean isToday(Todo findTodo) {
        return findTodo.getType().equals(Type.TODAY);
    }

    /**
     * 백로그 할 일을 TODAY 할 일로 변경합니다.
     *
     * @param todo 변경할 할 일 객체
     */
    private void swipeBacklogToToday(Todo todo) {
        Integer maxTodayOrder = todoRepository.findMaxTodayOrderByUserIdOrZero(todo.getUserId());
        todo.changeToToday(maxTodayOrder);
    }

    /**
     * TODAY 할 일을 백로그로 변경합니다.
     *
     * @param todo 변경할 할 일 객체
     */
    private void swipeTodayToBacklog(Todo todo) {
        if (isCompletedTodo(todo)) {
            throw new CustomException(TodoErrorStatus._ALREADY_COMPLETED_TODO);
        }
        Integer maxBacklogOrder = todoRepository.findMaxBacklogOrderByUserIdOrZero(todo.getUserId());
        todo.changeToBacklog(maxBacklogOrder);
    }

    /**
     * 특정 할 일이 완료 상태인지 확인합니다.
     *
     * @param todo 확인할 할 일 객체
     * @return 완료 상태이면 true, 아니면 false
     */
    private boolean isCompletedTodo(Todo todo) {
        return todo.getTodayStatus().equals(TodayStatus.COMPLETED);
    }

    /**
     * 할 일의 순서를 드래그 앤 드롭 방식으로 변경합니다.
     *
     * @param userId 사용자 ID
     * @param requestDto 순서 변경 요청 데이터
     */
    public void dragAndDrop(Long userId, TodoDragAndDropRequestDto requestDto) {
        userValidator.checkIsExistUser(userId);
        List<Todo> todos = requestDto.todoIds().stream()
                .map(todoId -> todoRepository.findById(todoId)
                        .orElseThrow(() -> new CustomException(TodoErrorStatus._TODO_NOT_EXIST)))
                .collect(Collectors.toList());
        checkIsValidToDragAndDrop(userId, todos, requestDto);
        if (isTypeToday(requestDto.type())) {
            reassignTodayOrder(todos);
            return;
        }
        reassignBacklogOrder(todos);
    }

    /**
     * 드래그 앤 드롭을 수행할 수 있는지 유효성 검사합니다.
     * - 사용자가 해당 할 일의 소유자인지 확인
     * - TODAY 할 일 중 이미 완료된 항목이 포함되어 있는지 확인
     *
     * @param userId 사용자 ID
     * @param todos 검사할 할 일 목록
     * @param requestDto 드래그 앤 드롭 요청 데이터
     */
    private void checkIsValidToDragAndDrop(Long userId, List<Todo> todos, TodoDragAndDropRequestDto requestDto) {
        for (Todo todo : todos) {
            if (!todo.getUserId().equals(userId)) {
                throw new CustomException(TodoErrorStatus._TODO_USER_NOT_MATCH);
            }
            if (requestDto.type().equals(Type.TODAY) && todo.getTodayStatus() == TodayStatus.COMPLETED) {
                throw new CustomException(TodoErrorStatus._ALREADY_COMPLETED_TODO);
            }
        }
    }

    /**
     * TODAY 할 일의 순서를 재할당합니다.
     *
     * @param todos 재할당할 할 일 목록
     */
    private void reassignTodayOrder(List<Todo> todos) {
        List<Integer> todayOrders = getTodayOrders(todos);
        todayOrders.sort(Collections.reverseOrder());
        for (int i = 0; i < todos.size(); i++) {
            todos.get(i).setTodayOrder(todayOrders.get(i));
            todoRepository.save(todos.get(i));
        }
    }

    /**
     * BACKLOG 할 일의 순서를 재할당합니다.
     *
     * @param todos 재할당할 할 일 목록
     */
    private void reassignBacklogOrder(List<Todo> todos) {
        List<Integer> backlogOrders = getBacklogOrders(todos);
        backlogOrders.sort(Collections.reverseOrder());
        for (int i = 0; i < todos.size(); i++) {
            todos.get(i).setBacklogOrder(backlogOrders.get(i));
            todoRepository.save(todos.get(i));
        }
    }

    /**
     * TODAY 할 일의 정렬 순서를 가져옵니다.
     *
     * @param todos 할 일 목록
     * @return todayOrder 값 목록
     */
    private List<Integer> getTodayOrders(List<Todo> todos) {
        List<Integer> todayOrders = new ArrayList<>();
        for (Todo todo : todos) {
            todayOrders.add(todo.getTodayOrder());
        }
        return todayOrders;
    }

    /**
     * BACKLOG 할 일의 정렬 순서를 가져옵니다.
     *
     * @param todos 할 일 목록
     * @return backlogOrder 값 목록
     */
    private List<Integer> getBacklogOrders(List<Todo> todos) {
        List<Integer> backlogOrders = new ArrayList<>();
        for (Todo todo : todos) {
            backlogOrders.add(todo.getBacklogOrder());
        }
        return backlogOrders;
    }

    /**
     * 주어진 Type이 TODAY인지 확인합니다.
     *
     * @param type 확인할 Type 값
     * @return Type이 TODAY이면 true, 아니면 false
     */
    private boolean isTypeToday(Type type) {
        return type.equals(Type.TODAY);
    }

    /**
     * 특정 할 일의 상세 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 조회할 할 일 ID
     * @return 할 일 상세 정보
     */
    public TodoDetailResponseDto getTodoInfo(Long userId, Long todoId) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        Category findCategory = findTodo.getCategoryId() != null ?
                categoryRepository.findById(findTodo.getCategoryId()).orElse(null) : null;
        Emoji findEmoji = findCategory != null && findCategory.getEmojiId() != null ?
                emojiRepository.findById(findCategory.getEmojiId()).orElse(null) : null;
        return TodoDetailResponseDto.of(findTodo, findCategory, findEmoji);
    }

    /**
     * 특정 할 일의 마감 기한을 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 업데이트할 할 일 ID
     * @param requestDto 마감 기한 업데이트 요청 데이터
     */
    public void updateDeadline(Long userId, Long todoId, DeadlineUpdateRequestDto requestDto) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        findTodo.updateDeadline(requestDto.deadline());
    }

    /**
     * 특정 할 일의 내용을 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 업데이트할 할 일 ID
     * @param requestDto 내용 업데이트 요청 데이터
     */
    public void updateContent(Long userId, Long todoId, ContentUpdateRequestDto requestDto) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        findTodo.updateContent(requestDto.content());
    }

    /**
     * 특정 할 일의 완료 상태를 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 업데이트할 할 일 ID
     * @param now 현재 시간
     */
    public void updateIsCompleted(Long userId, Long todoId, LocalDateTime now) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        if (isTypeYesterday(findTodo.getType())) {
            updateYesterdayIsCompleted(findTodo);
            return;
        }
        if (isTypeToday(findTodo.getType())) {
            updateTodayIsCompleted(findTodo, now);
        }
    }

    /**
     * 특정 할 일의 어제 완료 상태를 업데이트합니다.
     *
     * - 할 일이 미완료(INCOMPLETE) 상태라면, 어제 완료(COMPLETED) 상태로 변경하고
     *   완료 시간을 "어제 날짜의 23:59"로 저장합니다.
     * - 할 일이 완료(COMPLETED) 상태라면, 다시 미완료(INCOMPLETE) 상태로 변경하고
     *   백로그 순서를 고려하여 업데이트합니다.
     * - 완료된 날짜 기록(CompletedDateTime)이 존재하지 않으면 예외를 발생시킵니다.
     *
     * @param findTodo 업데이트할 할 일 객체
     */
    private void updateYesterdayIsCompleted(Todo findTodo) {
        if (TodayStatus.INCOMPLETE.equals(findTodo.getTodayStatus())) {
            // 어제 날짜를 23:59로 설정하여 완료 상태 업데이트
            LocalDateTime yesterday = LocalDateTime.of(findTodo.getTodayDate(), LocalTime.of(23, 59));
            findTodo.updateYesterdayToCompleted();
            completedDateTimeRepository.save(new CompletedDateTime(findTodo.getId(), yesterday));
            return;
        }
        if (TodayStatus.COMPLETED.equals(findTodo.getTodayStatus())) {
            // 미완료로 변경하며, 백로그의 최소 순서를 가져와 반영
            Integer minBacklogOrder = todoRepository.findMinBacklogOrderByUserIdOrZero(findTodo.getUserId());
            findTodo.updateYesterdayToInComplete(minBacklogOrder);

            // 기존 완료 기록이 존재하면 삭제, 없으면 예외 발생
            CompletedDateTime completedDateTime = completedDateTimeRepository.findByDateAndTodoId(findTodo.getId(), findTodo.getTodayDate())
                    .orElseThrow(() -> new CustomException(TodoErrorStatus._COMPLETED_DATETIME_NOT_EXIST));
            completedDateTimeRepository.delete(completedDateTime);
        }
    }

    /**
     * 특정 할 일의 오늘 완료 상태를 업데이트합니다.
     *
     * - 할 일이 미완료(INCOMPLETE) 상태라면, 오늘 완료(COMPLETED) 상태로 변경하고
     *   완료 시간을 현재 시간(now)으로 저장합니다.
     * - 할 일이 완료(COMPLETED) 상태라면, 다시 미완료(INCOMPLETE) 상태로 변경하고
     *   오늘의 최소 순서를 고려하여 업데이트합니다.
     * - 완료된 날짜 기록(CompletedDateTime)이 존재하지 않으면 예외를 발생시킵니다.
     *
     * @param findTodo 업데이트할 할 일 객체
     * @param now 현재 시간
     */
    private void updateTodayIsCompleted(Todo findTodo, LocalDateTime now) {
        if (TodayStatus.INCOMPLETE.equals(findTodo.getTodayStatus())) {
            // 오늘 완료 상태로 변경하고 현재 시간을 완료 시간으로 저장
            findTodo.updateTodayToCompleted();
            completedDateTimeRepository.save(new CompletedDateTime(findTodo.getId(), now));
            return;
        }
        if (TodayStatus.COMPLETED.equals(findTodo.getTodayStatus())) {
            // 미완료로 변경하며, 오늘의 최소 순서를 가져와 반영
            Integer minTodayOrder = todoRepository.findMinTodayOrderByUserIdOrZero(findTodo.getUserId());
            findTodo.updateTodayToInComplete(minTodayOrder);

            // 기존 완료 기록이 존재하면 삭제, 없으면 예외 발생
            CompletedDateTime completedDateTime = completedDateTimeRepository.findByDateAndTodoId(findTodo.getId(), findTodo.getTodayDate())
                    .orElseThrow(() -> new CustomException(TodoErrorStatus._COMPLETED_DATETIME_NOT_EXIST));
            completedDateTimeRepository.delete(completedDateTime);
        }
    }

    /**
     * 주어진 타입이 "어제(YESTERDAY)"인지 확인합니다.
     *
     * @param type 확인할 타입
     * @return 주어진 타입이 YESTERDAY이면 true, 그렇지 않으면 false
     */
    private boolean isTypeYesterday(Type type) {
        return type.equals(Type.YESTERDAY);
    }

    /**
     * 히스토리 데이터를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param localDate 조회할 날짜
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 히스토리 데이터
     */
    public PaginatedHistoryResponseDto getHistories(Long userId, LocalDate localDate, int page, int size) {
        userValidator.checkIsExistUser(userId);
        Page<Todo> historiesPage = todoRepository.findHistories(userId, localDate, PageRequest.of(page, size));
        return PaginatedHistoryResponseDto.of(historiesPage);
    }

    /**
     * 히스토리 캘린더 데이터를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param year 조회할 연도
     * @param month 조회할 월
     * @return 캘린더 데이터
     */
    public HistoryCalendarListResponseDto getHistoriesCalendar(Long userId, String year, int month) {
        List<LocalDate> dates = completedDateTimeRepository.findHistoryExistingDates(userId, year, month).stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .toList();
        return HistoryCalendarListResponseDto.of(dates);
    }

    /**
     * 특정 할 일의 카테고리를 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 업데이트할 할 일 ID
     * @param requestDto 카테고리 업데이트 요청 데이터
     */
    public void updateCategory(Long userId, Long todoId, TodoCategoryUpdateRequestDto requestDto) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        if (requestDto.categoryId() != null) {
            categoryValidator.validateCategory(userId, requestDto.categoryId());
        }
        findTodo.updateCategory(requestDto.categoryId());
    }

    /**
     * 특정 할 일의 반복 설정을 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 업데이트할 할 일 ID
     */
    public void updateRepeat(Long userId, Long todoId) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        findTodo.updateIsRepeat();
    }
}
