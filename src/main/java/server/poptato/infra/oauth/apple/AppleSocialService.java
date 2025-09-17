package server.poptato.infra.oauth.apple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.infra.oauth.SocialService;
import server.poptato.infra.oauth.SocialUserInfo;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppleSocialService extends SocialService {

    private final AppleTokenVerifier appleTokenVerifier;

    /**
     * Apple OAuth를 통해 사용자 정보를 가져옵니다.
     * - sub 값을 id_token에서 추출하여 유저를 식별.
     * - name과 email은 id_token에 포함되지 않으므로, 프론트에서 받은 값을 그대로 사용.
     *
     * @param request 사용자의 로그인 요청 정보 (프론트에서 받은 name, email 포함)
     * @return 소셜 사용자 정보 객체
     */
    @Override
    public SocialUserInfo getUserData(LoginRequestDto request) {
        var userInfoObject = appleTokenVerifier.verifyIdToken(request.accessToken());

        return new SocialUserInfo(
                userInfoObject.get("sub").getAsString(),  // 소셜 ID
                request.name(),
                request.email(),
                null
        );
    }
}
