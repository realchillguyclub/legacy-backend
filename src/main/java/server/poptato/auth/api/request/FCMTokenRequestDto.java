package server.poptato.auth.api.request;

import jakarta.validation.constraints.NotBlank;

public record FCMTokenRequestDto(
        @NotBlank(message = "fcm 토큰은 필수입니다.")
        String clientId
) {
}
