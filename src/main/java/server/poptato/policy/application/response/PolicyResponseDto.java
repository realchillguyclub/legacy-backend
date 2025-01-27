package server.poptato.policy.application.response;

import lombok.Builder;
import server.poptato.policy.domain.entity.Policy;

import java.time.LocalDateTime;

@Builder
public record PolicyResponseDto(
        Long id,
        String content,
        LocalDateTime createdAt
) {

    public static PolicyResponseDto from(Policy policy) {
        return new PolicyResponseDto(
                policy.getId(),
                policy.getContent(),
                policy.getCreatedAt()
        );
    }
}
