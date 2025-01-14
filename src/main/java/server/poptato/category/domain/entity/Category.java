package server.poptato.category.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import server.poptato.category.api.request.CategoryCreateUpdateRequestDto;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private Long emojiId;
    @NotNull
    private int categoryOrder;
    @NotNull
    private String name;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate;
    @LastModifiedDate
    private LocalDateTime modifyDate;

    public static Category create(Long userId, int maxCategoryId, CategoryCreateUpdateRequestDto request) {
        return Category.builder()
                .userId(userId)
                .categoryOrder(++maxCategoryId)
                .emojiId(request.emojiId())
                .name(request.name())
                .build();

    }

    public void update(CategoryCreateUpdateRequestDto updateRequestDto) {
        this.name = updateRequestDto.name();
        this.emojiId = updateRequestDto.emojiId();
    }

    public void setCategoryOrder(int categoryOrder) {
        this.categoryOrder = categoryOrder;
    }
}
