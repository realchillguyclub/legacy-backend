package server.poptato.todo.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import server.poptato.todo.domain.value.Type;

import java.util.List;

public record TodoDragAndDropRequestDto(
        @NotNull(message = "드래그앤드롭 시 할 일 타입은 필수입니다.")
        Type type,
        @NotNull(message = "드래그앤드롭 시 할 일 리스트는 필수입니다.")
        @Size(min = 2, message = "할 일 리스트는 최소 2개 이상의 항목이 있어야 합니다.")
        List<Long> todoIds
){
}
