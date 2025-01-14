package server.poptato.category.application.response;

import lombok.Getter;
import server.poptato.category.domain.entity.Category;

@Getter
public class CategoryResponseDto {
    Long id;
    String name;

    Long emojiId;
    String imageUrl;

    public CategoryResponseDto(Category category,String imageUrl) {
        this.id = category.getId();
        this.name = category.getName();
        this.emojiId = category.getEmojiId();
        this.imageUrl = imageUrl;
    }
}
