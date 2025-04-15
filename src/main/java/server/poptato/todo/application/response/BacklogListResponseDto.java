package server.poptato.todo.application.response;

import org.springframework.data.domain.Page;
import server.poptato.category.domain.entity.Category;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.global.util.FileUtil;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.user.domain.value.MobileType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record BacklogListResponseDto(
        long totalCount,
        String categoryName,
        List<BacklogResponseDto> backlogs,
        int totalPageCount
){

    public static BacklogListResponseDto of(String categoryName, MobileType mobileType, Page<Todo> backlogs) {
        return new BacklogListResponseDto(
                backlogs.getTotalElements(),
                categoryName,
                backlogs.getContent().stream()
                        .map((backlog) -> {
                            Category category = backlog.getCategory();
                            String detailCategoryName = Optional.ofNullable(category)
                                    .map(Category::getName)
                                    .orElse(null);
                            String imageUrl = Optional.ofNullable(category)
                                    .map(Category::getEmoji)
                                    .map(Emoji::getImageUrl)
                                    .map((url) -> FileUtil.changeFileExtension(url, mobileType.getImageUrlExtension()))
                                    .orElse(null);

                            return BacklogResponseDto.of(backlog, detailCategoryName, imageUrl);
                        })
                        .collect(Collectors.toList()),
                backlogs.getTotalPages()
        );
    }
}