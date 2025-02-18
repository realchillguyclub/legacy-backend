package server.poptato.auth.api.request;

import jakarta.validation.constraints.NotBlank;

public record FCMTokenRequestDto(
        @NotBlank(message = "모바일 식별값은 필수입니다.")
        String clientId
) {
}
