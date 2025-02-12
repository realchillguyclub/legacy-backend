package server.poptato.user.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.external.oauth.SocialUserInfo;
import server.poptato.user.domain.value.SocialType;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @NotNull
    private String socialId;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @Nullable
    private String imageUrl;

    @NotNull
    private Boolean isPushAlarm;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static User create(LoginRequestDto request, SocialUserInfo userInfo, String imageUrl){
        return User.builder()
                .socialType(request.socialType())
                .isPushAlarm(true)
                .socialId(userInfo.socialId())
                .name(userInfo.name())
                .email(userInfo.email())
                .imageUrl(imageUrl)
                .build();
    }
}
