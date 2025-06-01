package server.poptato.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.poptato.global.dao.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "delete_reason")
public class DeleteReason extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "delete_reason")
    private String deleteReason;

    @Builder
    public DeleteReason(Long userId, String deleteReason) {
        this.userId = userId;
        this.deleteReason = deleteReason;
    }
}
