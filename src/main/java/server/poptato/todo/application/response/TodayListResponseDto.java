package server.poptato.todo.application.response;

import server.poptato.category.domain.entity.Category;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.global.util.FileUtil;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.user.domain.value.MobileType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record TodayListResponseDto(
        LocalDate date,
        List<TodayResponseDto> todays,
        int totalPageCount
) {
    public static TodayListResponseDto of(LocalDate date, MobileType mobileType, List<Todo> todos, int totalPageCount) {
        return new TodayListResponseDto(
                date,
                todos.stream()
                        .map((todo) -> {
                            Category category = todo.getCategory();
                            String name = Optional.ofNullable(category)
                                    .map(Category::getName)
                                    .orElse(null);
                            String imageUrl = Optional.ofNullable(category)
                                    .map(Category::getEmoji)
                                    .map(Emoji::getImageUrl)
                                    .map((url) -> FileUtil.changeFileExtension(url, mobileType.getImageUrlExtension()))
                                    .orElse(null);

                            return TodayResponseDto.of(todo, name, imageUrl);
                        })
                        .collect(Collectors.toList()),
                totalPageCount
        );
    }
}
