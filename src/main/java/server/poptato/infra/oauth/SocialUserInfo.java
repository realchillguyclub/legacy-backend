package server.poptato.infra.oauth;

public record SocialUserInfo(
        String socialId,
        String name,
        String email,
        String imageUrl
) {
}
