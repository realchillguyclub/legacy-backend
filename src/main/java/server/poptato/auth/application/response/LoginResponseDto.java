package server.poptato.auth.application.response;

import lombok.Builder;

@Builder
public record LoginResponseDto(String accessToken, String refreshToken, boolean isNewUser, Long userId) {
}
