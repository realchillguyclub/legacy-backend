package server.poptato.external.oauth;

import server.poptato.auth.api.request.LoginRequestDto;

public abstract class SocialService {
    public abstract SocialUserInfo getUserData(LoginRequestDto request);
}
