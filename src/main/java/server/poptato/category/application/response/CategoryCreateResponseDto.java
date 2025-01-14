package server.poptato.category.application.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryCreateResponseDto {
    Long categoryId;
}
