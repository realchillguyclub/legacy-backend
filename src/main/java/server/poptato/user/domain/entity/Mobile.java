package server.poptato.user.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.user.domain.value.MobileType;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Mobile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    Long userId;

    @Enumerated(EnumType.STRING)
    MobileType type;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    @NotNull
    private String clientId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

    public void setModifyDate(LocalDateTime modifyDate){
        this.modifyDate = modifyDate;
    }
    public void setClientId(String client_id){
        this.clientId = client_id;
    }
    public static Mobile create(LoginRequestDto requestDto, Long userId) {
        return Mobile.builder()
                .userId(userId)
                .type(requestDto.mobileType())
                .clientId(requestDto.clientId())
                .build();
    }
}
