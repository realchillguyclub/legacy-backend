package server.poptato.todo.application.response;

import java.util.List;

public record BacklogListResponseDto(
        long totalCount,
        String categoryName,
        List<BacklogResponseDto> backlogs,
        int totalPageCount
){

    public static BacklogListResponseDto of(
            String categoryName,
            List<BacklogResponseDto> backlogDtos,
            long totalCount,
            int totalPageCount
    ) {
        return new BacklogListResponseDto(
                totalCount,
                categoryName,
                backlogDtos,
                totalPageCount
        );
    }
}
