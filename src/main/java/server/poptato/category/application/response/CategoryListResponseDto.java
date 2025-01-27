package server.poptato.category.application.response;


import java.util.List;

public record CategoryListResponseDto(
        List<CategoryResponseDto> categories,
        int totalPageCount
) {
}
