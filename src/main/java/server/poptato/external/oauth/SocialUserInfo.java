package server.poptato.external.oauth;

public record SocialUserInfo(
        String socialId,
        String nickname,
        String email,
        String imageUrl
) {
}
