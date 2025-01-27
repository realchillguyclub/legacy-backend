package server.poptato.category.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 카테고리 생성/수정 요청 DTO.
 * 카테고리 이름과 이모지 ID를 포함합니다.
 */
public record CategoryCreateUpdateRequestDto(
        @NotBlank(message = "카테고리 이름은 비어 있을 수 없습니다.") String name,
        @NotNull(message = "이모지 ID는 null일 수 없습니다.") Long emojiId
) {
}
