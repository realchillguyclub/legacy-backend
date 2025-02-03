package server.poptato.category.application.response;

import server.poptato.category.domain.entity.Category;

public record CategoryResponseDto (
        Long id,
        String name,
        Long emojiId,
        String imageUrl
) {

    public static CategoryResponseDto of(Category category, String imageUrl) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getEmojiId(),
                imageUrl
        );
    }
}
