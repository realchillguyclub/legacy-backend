package server.poptato.external.oauth;

public abstract class SocialService {
    public abstract SocialUserInfo getUserData(String accessToken);
}
