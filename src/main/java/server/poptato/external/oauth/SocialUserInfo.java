package server.poptato.external.oauth;

public record SocialUserInfo(
        String socialId,
        String name,
        String email,
        String imageUrl
) {
}
