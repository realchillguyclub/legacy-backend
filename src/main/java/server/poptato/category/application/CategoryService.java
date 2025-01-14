package server.poptato.category.application;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.category.api.request.CategoryCreateUpdateRequestDto;
import server.poptato.category.api.request.CategoryDragAndDropRequestDto;
import server.poptato.category.application.response.CategoryCreateResponseDto;
import server.poptato.category.application.response.CategoryListResponseDto;
import server.poptato.category.application.response.CategoryResponseDto;
import server.poptato.category.domain.entity.Category;
import server.poptato.category.domain.repository.CategoryRepository;
import server.poptato.category.exception.CategoryException;
import server.poptato.category.validator.CategoryValidator;
import server.poptato.emoji.domain.repository.EmojiRepository;
import server.poptato.emoji.validator.EmojiValidator;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.todo.exception.TodoException;
import server.poptato.todo.exception.errorcode.TodoExceptionErrorCode;
import server.poptato.user.validator.UserValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static server.poptato.category.exception.errorcode.CategoryExceptionErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserValidator userValidator;
    private final EmojiValidator emojiValidator;
    private final CategoryValidator categoryValidator;
    private final EmojiRepository emojiRepository;
    private final TodoRepository todoRepository;

    private static final Long ALL_CATEGORY = -1L;
    private static final Long BOOKMARK_CATEGORY = 0L;

    public CategoryCreateResponseDto createCategory(Long userId, CategoryCreateUpdateRequestDto request) {
        userValidator.checkIsExistUser(userId);
        emojiValidator.checkIsExistEmoji(request.emojiId());
        int maxCategoryId = categoryRepository.findMaxCategoryOrderByUserId(userId).orElseThrow(
                ()->new CategoryException(DEFAULT_CATEGORY_NOT_EXIST));
        Category newCategory = categoryRepository.save(Category.create(userId,maxCategoryId,request));
        return CategoryCreateResponseDto.builder().categoryId(newCategory.getId()).build();
    }

    public CategoryListResponseDto getCategories(Long userId, int page, int size) {
        userValidator.checkIsExistUser(userId);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Category> categories = categoryRepository.findCategories(userId, pageRequest);
        return convertToCategoryListDto(categories);
    }

    private CategoryListResponseDto convertToCategoryListDto(Page<Category> categories) {
        List<CategoryResponseDto> categoryResponseDtoList = categories.stream()
                .map(category -> {
                    String imageUrl = emojiRepository.findImageUrlById(category.getEmojiId());
                    return new CategoryResponseDto(category, imageUrl);
                })
                .collect(Collectors.toList());

        return new CategoryListResponseDto(categoryResponseDtoList, categories.getTotalPages());
    }

    public void updateCategory(Long userId, Long categoryId, CategoryCreateUpdateRequestDto updateRequestDto) {
        userValidator.checkIsExistUser(userId);
        emojiValidator.checkIsExistEmoji(updateRequestDto.emojiId());
        Category category = categoryValidator.validateAndReturnCategory(userId, categoryId);
        category.update(updateRequestDto);
        categoryRepository.save(category);
    }

    public void deleteCategory(Long userId, Long categoryId) {
        userValidator.checkIsExistUser(userId);
        Category category = categoryValidator.validateAndReturnCategory(userId, categoryId);
        categoryRepository.delete(category);
        todoRepository.deleteAllByCategoryId(categoryId);
    }

    public void dragAndDrop(Long userId, CategoryDragAndDropRequestDto request) {
        userValidator.checkIsExistUser(userId);
        List<Category> categories = getCategoriesByIds(request.getCategoryIds());
        checkIsValidToDragAndDrop(userId, categories, request);
        reassignCategoryOrder(categories, request.getCategoryIds());
    }

    private List<Category> getCategoriesByIds(List<Long> categoryIds) {
        List<Category> categories = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryException(CATEGORY_NOT_EXIST));
            categories.add(category);
        }
        return categories;
    }

    private void checkIsValidToDragAndDrop(Long userId, List<Category> categories, CategoryDragAndDropRequestDto request) {
        for (Category category : categories) {
            categoryValidator.validateCategory(userId, category.getId());
            if (category.getId()== ALL_CATEGORY || category.getId()==BOOKMARK_CATEGORY) {
                throw new CategoryException(INVALID_DRAG_AND_DROP_CATEGORY);
            }
        }
    }

    private void reassignCategoryOrder(List<Category> categories, List<Long> categoryIds) {
        List<Integer> categoryOrders = getCategoryOrders(categoryIds);
        Collections.sort(categoryOrders);
        for(int i=0;i<categories.size();i++){
            categories.get(i).setCategoryOrder(categoryOrders.get(i));
            categoryRepository.save(categories.get(i));
        }
    }

    private List<Integer> getCategoryOrders(List<Long> categoryIds) {
        List<Integer> categoryOrders = new ArrayList<>();
        for(Long categoryId : categoryIds){
            Category category = categoryRepository.findById(categoryId).get();
            categoryOrders.add(category.getCategoryOrder());
        }
        return categoryOrders;
    }
}
