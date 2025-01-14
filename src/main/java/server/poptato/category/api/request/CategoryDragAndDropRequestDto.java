package server.poptato.category.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDragAndDropRequestDto {
    @NotNull(message = "드래그앤드롭 시 카테고리 리스트는 필수입니다.")
    @Size(min = 2, message = "카테고리 리스트는 최소 2개 이상의 항목이 있어야 합니다.")
    List<Long> categoryIds;
}

