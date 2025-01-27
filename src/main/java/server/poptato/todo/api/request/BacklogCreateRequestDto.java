package server.poptato.todo.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BacklogCreateRequestDto (
        @NotBlank(message = "백로그 생성 시 내용은 필수입니다.")
        String content,
        @NotNull(message = "백로그 생성 시 카테고리 아이디는 필수입니다.")
        Long categoryId
){
}
