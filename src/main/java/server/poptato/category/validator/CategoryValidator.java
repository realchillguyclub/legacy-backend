package server.poptato.category.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import server.poptato.category.domain.entity.Category;
import server.poptato.category.domain.repository.CategoryRepository;
import server.poptato.category.status.CategoryErrorStatus;
import server.poptato.global.exception.CustomException;

/**
 * 카테고리 관련 유효성 검증을 처리하는 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    /**
     * 특정 카테고리를 검증하고, 검증에 성공하면 해당 카테고리를 반환합니다.
     * - 카테고리가 존재하지 않는 경우 예외 발생
     * - 사용자 ID와 카테고리 소유자 ID가 일치하지 않는 경우 예외 발생
     *
     * @param userId 사용자 ID
     * @param categoryId 검증할 카테고리 ID
     * @return 검증된 카테고리 객체
     * @throws CustomException {@link CategoryErrorStatus#_CATEGORY_NOT_EXIST}, {@link CategoryErrorStatus#_CATEGORY_USER_NOT_MATCH} 예외 발생
     */
    public Category validateAndReturnCategory(Long userId, Long categoryId) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(CategoryErrorStatus._CATEGORY_NOT_EXIST));

        if (findCategory.getUserId() != userId && findCategory.getUserId() != -1L) {
            log.warn("🚨 Category ownership mismatch! categoryId={}, requested by userId={}, but belongs to userId={}",
                    categoryId, userId, findCategory.getUserId());
            throw new CustomException(CategoryErrorStatus._CATEGORY_USER_NOT_MATCH);
        }
        return findCategory;
    }

    /**
     * 특정 카테고리를 검증합니다.
     * - 카테고리가 존재하지 않는 경우 예외 발생
     * - 사용자 ID와 카테고리 소유자 ID가 일치하지 않는 경우 예외 발생
     *
     * @param userId 사용자 ID
     * @param categoryId 검증할 카테고리 ID
     * @throws CustomException {@link CategoryErrorStatus#_CATEGORY_NOT_EXIST}, {@link CategoryErrorStatus#_CATEGORY_USER_NOT_MATCH} 예외 발생
     */
    public void validateCategory(Long userId, Long categoryId) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(CategoryErrorStatus._CATEGORY_NOT_EXIST));

        if (findCategory.getUserId() != userId && findCategory.getUserId() != -1L) {
            log.warn("🚨 Validation failed! userId={} tried to access categoryId={} owned by userId={}",
                    userId, categoryId, findCategory.getUserId());
            throw new CustomException(CategoryErrorStatus._CATEGORY_USER_NOT_MATCH);
        }
    }
}
