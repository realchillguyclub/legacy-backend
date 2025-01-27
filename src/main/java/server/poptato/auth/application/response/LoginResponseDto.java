package server.poptato.auth.application.response;

public record LoginResponseDto(
        String accessToken,
        String refreshToken,
        boolean isNewUser,
        Long userId
) {

    public static LoginResponseDto of(String accessToken, String refreshToken, boolean isNewUser, Long userId) {
        return new LoginResponseDto(
                accessToken,
                refreshToken,
                isNewUser,
                userId
        );
    }
}
