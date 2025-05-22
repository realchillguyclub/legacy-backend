package server.poptato.category.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.poptato.category.api.request.CategoryCreateUpdateRequestDto;
import server.poptato.emoji.domain.entity.Emoji;
import server.poptato.global.dao.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "emoji_id", nullable = false)
    private Long emojiId;

    @Column(name = "category_order", nullable = false)
    private int categoryOrder;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emoji_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Emoji emoji;

    @Builder
    public Category(Long userId, Long emojiId, int categoryOrder, String name) {
        this.userId = userId;
        this.emojiId = emojiId;
        this.categoryOrder = categoryOrder;
        this.name = name;
    }

    public void update(CategoryCreateUpdateRequestDto updateRequestDto) {
        this.name = updateRequestDto.name();
        this.emojiId = updateRequestDto.emojiId();
    }

    public void updateCategoryOrder(int categoryOrder) {
        this.categoryOrder = categoryOrder;
    }
}
