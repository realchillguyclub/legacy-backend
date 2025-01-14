package server.poptato.category.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.poptato.category.domain.entity.Category;
import server.poptato.category.domain.repository.CategoryRepository;
import server.poptato.category.exception.CategoryException;

import static server.poptato.category.exception.errorcode.CategoryExceptionErrorCode.CATEGORY_NOT_EXIST;
import static server.poptato.category.exception.errorcode.CategoryExceptionErrorCode.CATEGORY_USER_NOT_MATCH;

@Component
@RequiredArgsConstructor
public class CategoryValidator {
    private final CategoryRepository categoryRepository;

    public Category validateAndReturnCategory(Long userId, Long categoryId) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_EXIST));
        if (findCategory.getUserId() != userId && findCategory.getUserId() != -1L) throw new CategoryException(CATEGORY_USER_NOT_MATCH);
        return findCategory;
    }

    public void validateCategory(Long userId, Long categoryId) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_EXIST));
        if (findCategory.getUserId() != userId && findCategory.getUserId() != -1L) throw new CategoryException(CATEGORY_USER_NOT_MATCH);
    }
}
