package server.poptato.todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.category.domain.repository.CategoryRepository;
import server.poptato.category.validator.CategoryValidator;
import server.poptato.todo.api.request.BacklogCreateRequestDto;
import server.poptato.todo.application.response.*;
import server.poptato.todo.converter.TodoDtoConverter;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.CompletedDateTimeRepository;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.user.validator.UserValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class TodoBacklogService {
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final UserValidator userValidator;
    private final CategoryValidator categoryValidator;
    private static final Long ALL_CATEGORY = -1L;
    private static final Long BOOKMARK_CATEGORY = 0L;

    public BacklogListResponseDto getBacklogList(Long userId, Long categoryId, int page, int size) {
        userValidator.checkIsExistUser(userId);
        categoryValidator.validateCategory(userId, categoryId);
        Page<Todo> backlogs = getBacklogsPagination(userId, categoryId, page, size);
        String categoryName = categoryRepository.findById(categoryId).get().getName();
        return TodoDtoConverter.toBacklogListDto(categoryName, backlogs);
    }

    public BacklogCreateResponseDto generateBacklog(Long userId, BacklogCreateRequestDto backlogCreateRequestDto) {
        userValidator.checkIsExistUser(userId);
        categoryValidator.validateCategory(userId, backlogCreateRequestDto.getCategoryId());
        Integer maxBacklogOrder = todoRepository.findMaxBacklogOrderByUserIdOrZero(userId);
        Todo newBacklog = createNewBacklog(userId, backlogCreateRequestDto, maxBacklogOrder);
        return TodoDtoConverter.toBacklogCreateDto(newBacklog);
    }

    public PaginatedYesterdayResponseDto getYesterdays(Long userId, int page, int size) {
        userValidator.checkIsExistUser(userId);

        Pageable pageable = PageRequest.of(page, size);
        Page<Todo> yesterdaysPage = todoRepository.findByUserIdAndTypeAndTodayStatus(userId, Type.YESTERDAY, TodayStatus.INCOMPLETE, pageable);

        return TodoDtoConverter.toYesterdayListDto(yesterdaysPage);
    }

    private Page<Todo> getBacklogsPagination(Long userId, Long categoryId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Type> types = List.of(Type.BACKLOG, Type.YESTERDAY);
        TodayStatus status = TodayStatus.COMPLETED;
        if (categoryId == ALL_CATEGORY) return todoRepository.findAllBacklogs(userId, types, status, pageRequest);
        if (categoryId == BOOKMARK_CATEGORY)
            return todoRepository.findBookmarkBacklogs(userId, types, status, pageRequest);
        return todoRepository.findBacklogsByCategoryId(userId, categoryId, types, status, pageRequest);
    }

    private Todo createNewBacklog(Long userId, BacklogCreateRequestDto backlogCreateRequestDto, Integer maxBacklogOrder) {
        Todo backlog = null;
        Long categoryId = backlogCreateRequestDto.getCategoryId();
        if(categoryId==ALL_CATEGORY)
            backlog = Todo.createBacklog(userId, backlogCreateRequestDto.getContent(), maxBacklogOrder + 1);
        if(categoryId==BOOKMARK_CATEGORY)
            backlog = Todo.createBookmarkBacklog(userId, backlogCreateRequestDto.getContent(), maxBacklogOrder + 1);
        if (categoryId>BOOKMARK_CATEGORY)
            backlog = Todo.createCategoryBacklog(userId, categoryId, backlogCreateRequestDto.getContent(), maxBacklogOrder + 1);
        Todo newBacklog = todoRepository.save(backlog);
        return newBacklog;
    }
}
