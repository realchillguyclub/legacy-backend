package server.poptato.category.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import server.poptato.category.domain.entity.Category;
import server.poptato.todo.domain.entity.Todo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Optional<Integer> findMaxCategoryOrderByUserId(Long userId);

    Category save(Category category);

    Page<Category> findDefaultAndByUserIdOrderByCategoryOrder(Long userId, Pageable pageable);

    Optional<Category> findById(Long categoryId);

    void delete(Category category);

    default Page<Category> findCategories(Long userId, Pageable pageable) {
        return findDefaultAndByUserIdOrderByCategoryOrder(userId, pageable);
    }
}
