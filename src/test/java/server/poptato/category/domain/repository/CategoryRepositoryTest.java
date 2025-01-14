package server.poptato.category.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import server.poptato.category.domain.entity.Category;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @DisplayName("카테고리 목록 조회 시, categoryOrder에 따라 오름차순 정렬되어 조회된다.")
    @Test
    void findCategories_Success() {
        //given
        Long userId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 6);

        //when
        Page<Category> categories = categoryRepository.findCategories(userId, pageRequest);

        //then
        assertThat(categories.getContent()).isNotEmpty();
        assertThat(categories.getContent().stream().allMatch(category -> category.getUserId().equals(userId) || category.getUserId().equals(-1L))).isTrue();
        for (int i = 0; i < categories.getContent().size() - 1; i++) {
            assertThat(categories.getContent().get(i).getCategoryOrder()).isLessThan(categories.getContent().get(i + 1).getCategoryOrder());
        }
    }
}
