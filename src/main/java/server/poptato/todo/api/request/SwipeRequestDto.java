package server.poptato.todo.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwipeRequestDto {
    @NotNull(message = "할일 ID는 필수입니다.")
    Long todoId;
}
