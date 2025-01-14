package server.poptato.category.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryCreateUpdateRequestDto(@NotBlank String name, @NotNull Long emojiId){

}
