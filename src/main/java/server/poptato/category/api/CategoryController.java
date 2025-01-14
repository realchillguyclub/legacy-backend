package server.poptato.category.api;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import server.poptato.category.api.request.CategoryCreateUpdateRequestDto;
import server.poptato.category.api.request.CategoryDragAndDropRequestDto;
import server.poptato.category.application.CategoryService;
import server.poptato.category.application.response.CategoryCreateResponseDto;
import server.poptato.category.application.response.CategoryListResponseDto;
import server.poptato.global.response.BaseResponse;
import server.poptato.user.resolver.UserId;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public BaseResponse<CategoryCreateResponseDto> createCategory(@UserId Long userId,
                                                                  @Validated @RequestBody CategoryCreateUpdateRequestDto categoryCreateRequestDto) {
        CategoryCreateResponseDto response = categoryService.createCategory(userId, categoryCreateRequestDto);
        return new BaseResponse<>(response);
    }

    @GetMapping("/list")
    public BaseResponse<CategoryListResponseDto> getCategories(@UserId Long userId,
                                                               @RequestParam(value = "page", defaultValue = "0") int page,
                                                               @RequestParam(value = "size", defaultValue = "6") int size) {
        CategoryListResponseDto response = categoryService.getCategories(userId, page, size);
        return new BaseResponse<>(response);
    }

    @PutMapping("/{categoryId}")
    public BaseResponse updateCategory(@UserId Long userId,
                                       @PathVariable Long categoryId,
                                       @Validated @RequestBody CategoryCreateUpdateRequestDto categoryUpdateRequestDto) {
        categoryService.updateCategory(userId, categoryId, categoryUpdateRequestDto);
        return new BaseResponse<>();
    }
    @DeleteMapping("/{categoryId}")
    public BaseResponse deleteCategory(@UserId Long userId,
                                       @PathVariable Long categoryId) {
        categoryService.deleteCategory(userId, categoryId);
        return new BaseResponse<>();
    }

    @PatchMapping("/dragAndDrop")
    public BaseResponse dragAndDrop(@UserId Long userId,
                                    @Validated @RequestBody CategoryDragAndDropRequestDto categoryDragAndDropRequestDto) {
        categoryService.dragAndDrop(userId, categoryDragAndDropRequestDto);
        return new BaseResponse<>();
    }
}
