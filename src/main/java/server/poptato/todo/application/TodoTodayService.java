package server.poptato.todo.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.category.domain.entity.Category;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.global.util.FileUtil;
import server.poptato.todo.application.response.TodayListResponseDto;
import server.poptato.todo.application.response.TodayResponseDto;
import server.poptato.todo.domain.entity.Routine;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.RoutineRepository;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.user.domain.value.MobileType;
import server.poptato.user.validator.UserValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class TodoTodayService {
    private final TodoRepository todoRepository;
    private final RoutineRepository routineRepository;
    private final UserValidator userValidator;

    /**
     * 오늘의 할 일 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param page 요청 페이지 번호
     * @param size 한 페이지에 보여줄 항목 수
     * @param todayDate 오늘 날짜
     * @return TodayListResponseDto 오늘의 할 일 목록과 페이지 정보
     */
    public TodayListResponseDto getTodayList(
            long userId, MobileType mobileType, int page, int size, LocalDate todayDate
    ) {
        userValidator.checkIsExistUser(userId);

        List<Todo> todays = getAllTodays(userId, todayDate);
        List<Todo> todaySubList = getTodayPagination(todays, page, size);
        int totalPageCount = (int) Math.ceil((double) todays.size() / size);

        List<TodayResponseDto> todayDtos = todaySubList.stream()
                .map(todo -> {
                    String categoryName = Optional.ofNullable(todo.getCategory())
                            .map(Category::getName)
                            .orElse(null);

                    String imageUrl = Optional.ofNullable(todo.getCategory())
                            .map(Category::getEmoji)
                            .map(Emoji::getImageUrl)
                            .map(url -> FileUtil.changeFileExtension(url, mobileType.getImageUrlExtension()))
                            .orElse(null);

                    List<String> routineDays = routineRepository.findAllByTodoId(todo.getId())
                            .stream()
                            .map(Routine::getDay)
                            .collect(Collectors.toList());

                    return TodayResponseDto.of(todo, categoryName, imageUrl, routineDays);
                })
                .collect(Collectors.toList());

        return TodayListResponseDto.of(todayDate, todayDtos, totalPageCount);
    }

    /**
     * 오늘의 할 일 목록을 페이징 처리합니다.
     *
     * @param todays 오늘의 모든 할 일 목록
     * @param page 요청 페이지 번호
     * @param size 한 페이지에 보여줄 항목 수
     * @return 페이징 처리된 할 일 목록
     */
    private List<Todo> getTodayPagination(List<Todo> todays, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, todays.size());

        if (start >= end) {
            return new ArrayList<>();
        }

        return todays.subList(start, end);
    }

    /**
     * 사용자의 오늘의 모든 할 일을 조회합니다.
     * 완료된 항목과 미완료 항목을 포함합니다.
     *
     * @param userId 사용자 ID
     * @param todayDate 오늘 날짜
     * @return 오늘의 모든 할 일 목록
     */
    private List<Todo> getAllTodays(long userId, LocalDate todayDate) {
        List<Todo> todays = new ArrayList<>();
        List<Todo> incompleteTodos = todoRepository.findIncompleteTodaysWithCategory(userId, Type.TODAY, todayDate, TodayStatus.INCOMPLETE);
        List<Todo> completedTodos = todoRepository.findCompletedTodaysWithCategory(userId, todayDate);

        todays.addAll(incompleteTodos);
        todays.addAll(completedTodos);

        return todays;
    }
}
