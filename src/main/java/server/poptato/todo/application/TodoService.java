package server.poptato.todo.application;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.category.domain.entity.Category;
import server.poptato.category.domain.repository.CategoryRepository;
import server.poptato.category.validator.CategoryValidator;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.emoji.domain.repository.EmojiRepository;
import server.poptato.global.exception.CustomException;
import server.poptato.global.util.FileUtil;
import server.poptato.todo.api.request.*;
import server.poptato.todo.application.response.HistoryCalendarListResponseDto;
import server.poptato.todo.application.response.PaginatedHistoryResponseDto;
import server.poptato.todo.application.response.TodoDetailResponseDto;
import server.poptato.todo.domain.entity.CompletedDateTime;
import server.poptato.todo.domain.entity.Routine;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.CompletedDateTimeRepository;
import server.poptato.todo.domain.repository.RoutineRepository;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.todo.status.TodoErrorStatus;
import server.poptato.user.domain.value.MobileType;
import server.poptato.user.validator.UserValidator;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class TodoService {
    private final UserValidator userValidator;
    private final CategoryValidator categoryValidator;
    private final TodoRepository todoRepository;
    private final RoutineRepository routineRepository;
    private final CompletedDateTimeRepository completedDateTimeRepository;
    private final CategoryRepository categoryRepository;
    private final EmojiRepository emojiRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
        if (Type.TODAY == findTodo.getType()) {
            swipeTodayToBacklog(findTodo);
        } else if (Type.BACKLOG == findTodo.getType()) {
            swipeBacklogToToday(findTodo);
        }
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
        if (TodayStatus.COMPLETED == todo.getTodayStatus()) {
            throw new CustomException(TodoErrorStatus._ALREADY_COMPLETED_TODO);
        }
        Integer maxBacklogOrder = todoRepository.findMaxBacklogOrderByUserIdOrZero(todo.getUserId());
        todo.changeToBacklog(maxBacklogOrder);
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
                .map(todoId -> {
                    Todo todo = todoRepository.findById(todoId)
                            .orElseThrow(() -> new CustomException(TodoErrorStatus._TODO_NOT_EXIST));
                    if (!todo.getUserId().equals(userId)) {
                        // 사용자의 할 일이 아닌 경우
                        throw new CustomException(TodoErrorStatus._TODO_USER_NOT_MATCH);
                    }
                    return todo;
                })
                .toList();

        if (Type.TODAY == requestDto.type()) {
            reassignOrder(todos, Todo::getTodayOrder, Todo::updateTodayOrder);
        } else if (Type.BACKLOG == requestDto.type()) {
            reassignOrder(todos, Todo::getBacklogOrder, Todo::updateBacklogOrder);
        }
    }

    /**
     * 할 일 목록의 정렬 순서를 재할당하는 공통 메서드.
     *
     * @param todos 재할당할 할 일 목록
     * @param getOrder 각 할 일의 기존 순서를 가져오는 함수
     * @param setOrder 각 할 일에 새로운 순서를 설정하는 함수
     */
    private void reassignOrder(List<Todo> todos,
                               Function<Todo, Integer> getOrder,
                               BiConsumer<Todo, Integer> setOrder) {
        // 기존 순서를 가져와 내림차순으로 정렬
        List<Integer> newOrders = todos.stream()
                .map(todo -> {
                    if (TodayStatus.COMPLETED == todo.getTodayStatus()) {
                        // 완료된 할 일은 순서를 -1로 설정
                        return -1;
                    }
                    return getOrder.apply(todo);
                })
                .sorted(Collections.reverseOrder())
                .toList();

        // 정렬된 순서를 각 할 일에 재할당하고 저장
        for (int todoIndex = 0; todoIndex < todos.size(); todoIndex++) {
            Todo todo = todos.get(todoIndex);
            if (TodayStatus.COMPLETED == todo.getTodayStatus()) {
                // 완료된 할 일은 순서 수정 x
                continue;
            }
            setOrder.accept(todo, newOrders.get(todoIndex));
            todoRepository.save(todo);
        }
    }

    /**
     * 특정 할 일의 상세 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 조회할 할 일 ID
     * @return 할 일 상세 정보
     */
    public TodoDetailResponseDto getTodoInfo(Long userId, MobileType mobileType, Long todoId) {
        userValidator.checkIsExistUser(userId);
        String imageUrlExtension = mobileType.getImageUrlExtension();
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        Category findCategory = findTodo.getCategoryId() != null ?
                categoryRepository.findById(findTodo.getCategoryId()).orElse(null) : null;
        Emoji findEmoji = findCategory != null && findCategory.getEmojiId() != null ?
                emojiRepository.findById(findCategory.getEmojiId()).orElse(null) : null;
        String modifiedImageUrl = findEmoji != null && findEmoji.getImageUrl() != null ?
                FileUtil.changeFileExtension(findEmoji.getImageUrl(), imageUrlExtension) : null;
        List<String> routineDays = routineRepository.findAllByTodoId(todoId).stream()
                .map(Routine::getDay)
                .toList();

        return TodoDetailResponseDto.of(findTodo, findCategory, modifiedImageUrl, routineDays);
    }

    /**
     * 특정 할 일의 시간을 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 업데이트할 할 일 ID
     * @param requestDto 시간 업데이트 요청 데이터
     */
    public void updateTime(Long userId, Long todoId, TimeUpdateRequestDto requestDto) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        findTodo.updateTime(requestDto.time());
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
     * 특정 할 일의 루틴을 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param todoId 업데이트할 할 일 ID
     * @param requestDto 시간 업데이트 요청 데이터
     */
    public void updateRoutine(Long userId, Long todoId, RoutineUpdateRequestDto requestDto) {
        userValidator.checkIsExistUser(userId);
        if (!todoRepository.existsById(todoId)) {
            throw new CustomException(TodoErrorStatus._TODO_NOT_EXIST);
        }

        routineRepository.deleteByTodoId(todoId);
        List<String> newDays = requestDto.routineDays();
        if (newDays != null && !newDays.isEmpty()) {
            List<Routine> routineDays = newDays.stream()
                    .map(day -> Routine.builder()
                            .todoId(todoId)
                            .day(day)
                            .build())
                    .toList();
            routineRepository.saveAll(routineDays);
        }
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
     */
    public void updateIsCompleted(Long userId, Long todoId) {
        userValidator.checkIsExistUser(userId);
        Todo findTodo = validateAndReturnTodo(userId, todoId);
        if (Type.YESTERDAY == findTodo.getType()) {
            updateYesterdayIsCompleted(findTodo);
        } else if (Type.TODAY == findTodo.getType()) {
            updateTodayIsCompleted(findTodo);
        }
    }

    /**
     * 특정 할 일의 어제 완료 상태를 업데이트합니다.
     * - 미완료(INCOMPLETE) 상태 → 완료(COMPLETED) 상태로 변경
     * - 완료 시간을 "어제 날짜의 23:59"로 설정
     * - 반복 할 일이면 새로운 백로그 할 일을 생성
     *
     * @param findTodo 업데이트할 할 일 객체
     */
    private void updateYesterdayIsCompleted(Todo findTodo) {
        // 완료 처리
        LocalDateTime yesterday = LocalDateTime.of(findTodo.getTodayDate(), LocalTime.of(23, 59));
        int existBacklogOrder = findTodo.getBacklogOrder();
        findTodo.updateYesterdayToCompleted();
        completedDateTimeRepository.save(
                CompletedDateTime.builder()
                        .todoId(findTodo.getId())
                        .build()
        );

        // 반복 할 일이라면, 새로운 객체를 백로그에 추가
        if (findTodo.isRepeat()) {
            Todo newRepeatedTodo = Todo.builder()
                    .userId(findTodo.getUserId())
                    .categoryId(findTodo.getCategoryId())
                    .content(findTodo.getContent())
                    .type(Type.BACKLOG)
                    .backlogOrder(existBacklogOrder)
                    .todayDate(LocalDate.now())
                    .isBookmark(findTodo.isBookmark())
                    .isRepeat(true)
                    .deadline(findTodo.getDeadline())
                    .build();
            todoRepository.save(newRepeatedTodo);
        }
    }

    /**
     * 특정 할 일의 오늘 완료 상태를 업데이트합니다.
     * - 할 일이 미완료(INCOMPLETE) 상태라면, 오늘 완료(COMPLETED) 상태로 변경하고
     *   완료 시간을 현재 시간(now)으로 저장합니다.
     * - 할 일이 완료(COMPLETED) 상태라면, 다시 미완료(INCOMPLETE) 상태로 변경하고
     *   오늘의 최소 순서를 고려하여 업데이트합니다.
     * - 완료된 날짜 기록(CompletedDateTime)이 존재하지 않으면 예외를 발생시킵니다.
     *
     * @param findTodo 업데이트할 할 일 객체
     */
    private void updateTodayIsCompleted(Todo findTodo) {
        if (TodayStatus.INCOMPLETE.equals(findTodo.getTodayStatus())) {
            // 오늘 완료 상태로 변경하고 현재 시간을 완료 시간으로 저장
            findTodo.updateTodayToCompleted();
            completedDateTimeRepository.save(
                    CompletedDateTime.builder()
                    .todoId(findTodo.getId())
                    .build()
            );
            return;
        }
        if (TodayStatus.COMPLETED.equals(findTodo.getTodayStatus())) {
            // 미완료로 변경하며, 오늘의 최소 순서를 가져와 반영
            Integer minTodayOrder = todoRepository.findMinTodayOrderByUserIdOrZero(findTodo.getUserId());
            findTodo.updateTodayToInComplete(minTodayOrder);

            // 기존 완료 기록이 존재하면 삭제, 없으면 예외 발생
            CompletedDateTime completedDateTime = completedDateTimeRepository.findByCreateDateAndTodoId(findTodo.getId(), findTodo.getTodayDate())
                    .orElseThrow(() -> new CustomException(TodoErrorStatus._COMPLETED_DATETIME_NOT_EXIST));
            completedDateTimeRepository.delete(completedDateTime);
        }
    }

    /**
     * 어제 한 일을 체크하고, 상태를 변경합니다.
     * - 체크된 할 일들은 `COMPLETED` 상태로 변경됩니다.
     * - 체크되지 않은 할 일들은 `BACKLOG`로 이동합니다.
     *
     * @param userId 사용자 ID
     * @param request 체크된 할 일 목록 DTO
     */
    public void checkYesterdayTodos(Long userId, CheckYesterdayTodosRequestDto request) {
        userValidator.checkIsExistUser(userId);
        List<Todo> allYesterdays = todoRepository.findIncompleteYesterdays(userId);
        List<Long> checkedTodoIds = request.todoIds();

        // 1. 체크된 할 일들 (미완료 -> 완료)
        List<Todo> completedTodos = allYesterdays.stream()
                .filter(todo -> checkedTodoIds.contains(todo.getId()))
                .peek(this::updateYesterdayIsCompleted)
                .toList();

        // 2. 체크되지 않은 할 일들 (BACKLOG로 이동)
        List<Todo> backloggedTodos = allYesterdays.stream()
                .filter(todo -> !checkedTodoIds.contains(todo.getId()))
                .peek(todo -> todo.updateType(Type.BACKLOG))
                .toList();

        completedTodos.forEach(todoRepository::save);
        backloggedTodos.forEach(todoRepository::save);
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
        if (localDate.isBefore(LocalDate.now())) {
            Page<Todo> historiesPage = todoRepository.findHistories(userId, localDate, PageRequest.of(page, size));
            return PaginatedHistoryResponseDto.of(historiesPage, true);
        } else if (localDate.isEqual(LocalDate.now())) {
            Page<Todo> historiesPage = getTodayTodos(userId, page, size);
            return PaginatedHistoryResponseDto.from(historiesPage);
        }
        Page<Todo> historiesPage = todoRepository.findDeadlineBacklogs(userId, localDate, PageRequest.of(page, size));
        return PaginatedHistoryResponseDto.of(historiesPage, false);
    }

    /**
     * 오늘(TODAY)의 할 일 목록을 조회합니다.
     * 오늘 날짜 기준으로 다음의 할 일들을 조회하여 반환합니다:
     * - 미완료 상태(INCOMPLETE)의 할 일
     * - 완료 상태(COMPLETED)의 할 일
     * 두 목록을 하나로 합친 후, 요청된 페이지 기준으로 페이징 처리합니다.
     *
     * @param userId 사용자 ID
     * @param page 요청 페이지 번호 (0부터 시작)
     * @param size 페이지당 항목 수
     * @return 오늘 할 일 목록의 Page 객체
     */
    private Page<Todo> getTodayTodos(Long userId, int page, int size) {
        List<Todo> todayTodos = new ArrayList<>();
        List<Todo> incompleteTodos = todoRepository.findIncompleteTodays(userId, Type.TODAY, LocalDate.now(), TodayStatus.INCOMPLETE);
        List<Todo> completedTodos = todoRepository.findCompletedTodays(userId, LocalDate.now());
        todayTodos.addAll(incompleteTodos);
        todayTodos.addAll(completedTodos);

        PageRequest pageRequest = PageRequest.of(page, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), todayTodos.size());
        return new PageImpl<>(todayTodos.subList(start, end), pageRequest, todayTodos.size());
    }

    /**
     * 히스토리 캘린더 데이터를 조회합니다 (v1 - Legacy).
     * 사용자에게 히스토리가 존재하는 날짜 리스트만 반환합니다.
     * - 앱 버전이 1.2.0 미만일 때 호출됩니다.
     *
     * @param userId 사용자 ID
     * @param year 조회할 연도
     * @param month 조회할 월
     * @return 할 일이 존재하는 날짜 리스트
     */
    public List<LocalDate> getLegacyHistoriesCalendar(Long userId, String year, int month) {
        return completedDateTimeRepository.findHistoryExistingDates(userId, year, month).stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .toList();
    }

    /**
     * 히스토리 캘린더 데이터를 조회합니다 (v2 - New).
     * 다음의 정보를 날짜별로 통합하여 반환합니다:
     * - 완료된 할 일이 존재하는 날짜 (히스토리)
     * - 마감기한이 설정된 백로그가 존재하는 날짜
     * 날짜별로 해당 날짜의 백로그 개수가 함께 포함되며,
     * 히스토리 날짜는 기본적으로 count -1로 표시됩니다.
     *
     * @param userId 사용자 ID
     * @param year 조회할 연도 (예: "2025")
     * @param month 조회할 월 (1~12)
     * @return 날짜별 히스토리/백로그 정보 DTO
     */
    public HistoryCalendarListResponseDto getHistoriesCalendar(Long userId, String year, int month) {
        Map<LocalDate, Integer> historyCountByDate =
                completedDateTimeRepository.findHistoryExistingDates(userId, year, month).stream()
                        .map(LocalDateTime::toLocalDate)
                        .distinct()
                        .collect(Collectors.toMap(
                                Function.identity(),
                                date -> -1
                        ));

        Map<LocalDate, Integer> backlogCountByDate = todoRepository.findDatesWithBacklogCount(userId, year, month).stream()
                .collect(Collectors.toMap(
                        t -> ((Date)t.get("date")).toLocalDate(),
                        t -> ((Number) t.get("count")).intValue()
                ));

        historyCountByDate.putAll(backlogCountByDate);

        return HistoryCalendarListResponseDto.from(historyCountByDate);
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

    /**
     * 마감기한이 된 백로그 할 일들을 오늘 할 일(TODAY)로 변경합니다.
     * - 백로그에서 마감기한(today)과 일치하는 할 일들을 찾아 TODAY 상태로 업데이트합니다.
     * - 기본적인 todayOrder 값을 설정하여 정렬 순서를 유지합니다.
     * - 엔티티 매니저를 활용하여 변경 사항을 즉시 반영하고, 영속성 컨텍스트를 비웁니다.
     *
     * @param today 오늘 날짜
     * @param userIds 업데이트할 사용자 ID 목록
     */
    @Transactional
    public void processUpdateDeadlineTodos(LocalDate today, List<Long> userIds) {
        int basicTodayOrder = 0;
        todoRepository.updateBacklogTodosToToday(today, userIds, basicTodayOrder);
        entityManager.flush();
        entityManager.clear();
    }
}
