package server.poptato.policy.application.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PolicyResponseDto(Long id, String content, LocalDateTime createdAt) {
}
