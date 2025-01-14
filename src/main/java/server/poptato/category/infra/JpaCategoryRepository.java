package server.poptato.category.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.poptato.category.domain.entity.Category;
import server.poptato.category.domain.repository.CategoryRepository;

import java.util.Optional;

public interface JpaCategoryRepository extends CategoryRepository, JpaRepository<Category, Long> {
    @Query("SELECT MAX(c.categoryOrder) FROM Category c WHERE c.userId = :userId OR c.userId = -1")
    Optional<Integer> findMaxCategoryOrderByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Category c WHERE c.userId = :userId OR c.userId = -1 ORDER BY c.categoryOrder ASC")
    Page<Category> findDefaultAndByUserIdOrderByCategoryOrder(@Param("userId") Long userId, Pageable pageable);

}
