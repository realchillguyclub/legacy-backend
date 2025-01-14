package server.poptato.external.oauth.kakao;


public record KakaoUserResponse(Long id, KakaoUserProperties properties, KakaoAccount kakao_account) {

    public record KakaoUserProperties(String nickname) {
    }

    public record KakaoAccount(String email, Profile profile) {
    }

    public record Profile(String nickname, String profile_image_url, String thumbnail_image_url) {
    }
}
