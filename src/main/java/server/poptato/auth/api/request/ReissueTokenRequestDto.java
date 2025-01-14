package server.poptato.auth.api.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReissueTokenRequestDto {
    //TODO: 사용안하는 필드
    @NotEmpty(message = "토큰 재발급 시 accessToken은 필수입니다.")
    String accessToken;
    @NotEmpty(message = "토큰 재발급 시 refreshToken은 필수입니다.")
    String refreshToken;
}
