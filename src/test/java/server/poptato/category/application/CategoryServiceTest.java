package server.poptato.category.application;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.category.api.request.CategoryCreateUpdateRequestDto;
import server.poptato.category.api.request.CategoryDragAndDropRequestDto;
import server.poptato.category.application.response.CategoryCreateResponseDto;
import server.poptato.category.application.response.CategoryListResponseDto;
import server.poptato.category.domain.entity.Category;
import server.poptato.category.domain.repository.CategoryRepository;
import server.poptato.category.exception.CategoryException;
import server.poptato.emoji.domain.repository.EmojiRepository;
import server.poptato.emoji.exception.EmojiException;
import server.poptato.emoji.validator.EmojiValidator;
import server.poptato.user.validator.UserValidator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static server.poptato.category.exception.errorcode.CategoryExceptionErrorCode.CATEGORY_USER_NOT_MATCH;
import static server.poptato.category.exception.errorcode.CategoryExceptionErrorCode.INVALID_DRAG_AND_DROP_CATEGORY;
import static server.poptato.emoji.exception.errorcode.EmojiExceptionErrorCode.EMOJI_NOT_EXIST;

@Transactional
@SpringBootTest
public class CategoryServiceTest {
    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    EmojiRepository emojiRepository;
    @Autowired
    UserValidator userValidator;
    @Autowired
    EmojiValidator emojiValidator;

    @DisplayName("카테고리 생성 시 성공한다.")
    @Test
    void createCategory_Success() {
        //given
        Long userId = 1L;
        String name = "카테고리";
        Long emojiId = 3L;
        CategoryCreateUpdateRequestDto request = new CategoryCreateUpdateRequestDto(name, emojiId);

        //when
        CategoryCreateResponseDto response = categoryService.createCategory(userId, request);

        //then
        assertThat(response.getCategoryId()).isEqualTo(51L);
    }

    @DisplayName("카테고리 생성 시 존재하지 않는 이모지이면 예외가 발생한다.")
    @Test
    void createCategory_Emoji_Not_Exist_Exception() {
        //given
        Long userId = 1L;
        String name = "카테고리";
        Long emojiId = 100L;
        CategoryCreateUpdateRequestDto request = new CategoryCreateUpdateRequestDto(name, emojiId);

        //then
        assertThatThrownBy(() -> categoryService.createCategory(userId, request))
                .isInstanceOf(EmojiException.class)
                .hasMessage(EMOJI_NOT_EXIST.getMessage());
    }

    @DisplayName("카테고리 목록 조회 시 성공한다.")
    @Test
    void getCategories_Success() {
        //given
        Long userId = 1L;
        int page = 0;
        int size = 6;

        //when
        CategoryListResponseDto response = categoryService.getCategories(userId, page, size);

        //then
        assertThat(response.categories().size()).isEqualTo(6);
    }

    @DisplayName("카테고리 순서 변경 시 categoryOrder를 성공적으로 재정렬한다.")
    @Test
    void category_dragAndDrop_Success() {
        //given
        Long userId = 1L;
        CategoryDragAndDropRequestDto request = CategoryDragAndDropRequestDto.builder()
                .categoryIds(List.of(4L, 5L, 7L, 10L))
                .build();

        //when
        categoryService.dragAndDrop(userId, request);

        //then
        Category categoryId4 = categoryRepository.findById(4L).get();
        Category categoryId5 = categoryRepository.findById(5L).get();
        Category categoryId7 = categoryRepository.findById(7L).get();
        Category categoryId10 = categoryRepository.findById(10L).get();

        assertThat(categoryId4.getCategoryOrder()).isEqualTo(4L);
        assertThat(categoryId5.getCategoryOrder()).isEqualTo(5L);
        assertThat(categoryId7.getCategoryOrder()).isEqualTo(6L);
        assertThat(categoryId10.getCategoryOrder()).isEqualTo(7L);
    }

    @DisplayName("카테고리 순서 변경 시 사용자의 카테고리가 아니면 예외가 발생한다.")
    @Test
    void category_dragAndDrop_CategoryUserNotMatch_Exception() {
        //given
        Long userId = 1L;
        CategoryDragAndDropRequestDto request = CategoryDragAndDropRequestDto.builder()
                .categoryIds(List.of(1L, 50L, 4L))
                .build();

        //when & then
        assertThatThrownBy(() -> categoryService.dragAndDrop(userId, request))
                .isInstanceOf(CategoryException.class)
                .hasMessage(CATEGORY_USER_NOT_MATCH.getMessage());
    }

    @DisplayName("카테고리 순서 변경 시 -1이 포함되면 예외가 발생한다.")
    @Test
    void category_dragAndDrop_AllCategoryId_Exception() {
        //given
        Long userId = 1L;
        CategoryDragAndDropRequestDto request = CategoryDragAndDropRequestDto.builder()
                .categoryIds(List.of(-1L, 1L, 4L))
                .build();
        //then
        assertThatThrownBy(() -> categoryService.dragAndDrop(userId, request))
                .isInstanceOf(CategoryException.class)
                .hasMessage(INVALID_DRAG_AND_DROP_CATEGORY.getMessage());
    }

    @DisplayName("카테고리 순서 변경 시 0이 포함되면 예외가 발생한다.")
    @Test
    void category_dragAndDrop_BookmarkCategoryId_Exception() {
        //given
        Long userId = 1L;
        CategoryDragAndDropRequestDto request = CategoryDragAndDropRequestDto.builder()
                .categoryIds(List.of(0L, 1L, 4L))
                .build();
        //then
        assertThatThrownBy(() -> categoryService.dragAndDrop(userId, request))
                .isInstanceOf(CategoryException.class)
                .hasMessage(INVALID_DRAG_AND_DROP_CATEGORY.getMessage());
    }
}
