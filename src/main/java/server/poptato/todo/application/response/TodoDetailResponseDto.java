package server.poptato.todo.application.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TodoDetailResponseDto(String content, LocalDate deadline, String categoryName, String emojiImageUrl, Boolean isBookmark, Boolean isRepeat) {
}