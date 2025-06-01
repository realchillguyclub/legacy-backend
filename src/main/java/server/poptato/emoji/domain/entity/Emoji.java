package server.poptato.emoji.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.poptato.emoji.domain.value.GroupName;
import server.poptato.global.dao.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "emoji")
public class Emoji extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_name")
    private GroupName groupName;

    @Builder
    public Emoji(String imageUrl, GroupName groupName) {
        this.imageUrl = imageUrl;
        this.groupName = groupName;
    }
}
