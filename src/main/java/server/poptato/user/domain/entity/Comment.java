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
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "content", nullable = false, length = 800)
    private String content;

    @Column(name = "contact_info", length = 100)
    private String contactInfo;

    @Builder
    public Comment(Long userId, String content, String contactInfo) {
        this.userId = userId;
        this.content = content;
        this.contactInfo = contactInfo;
    }
}
