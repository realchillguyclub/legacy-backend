package server.poptato.global.dto;

public record TokenPair(
        String accessToken, String refreshToken
) {
}
