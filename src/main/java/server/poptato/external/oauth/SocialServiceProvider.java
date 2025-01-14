package server.poptato.external.oauth;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.poptato.external.oauth.apple.AppleSocialService;
import server.poptato.external.oauth.kakao.KakaoSocialService;
import server.poptato.user.domain.value.SocialType;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SocialServiceProvider {

    private static final Map<SocialType, SocialService> socialServiceMap = new HashMap<>();
    private final KakaoSocialService kakaoSocialService;
    private final AppleSocialService appleSocialService;

    @PostConstruct
    void initializeSocialServiceMap() {
        socialServiceMap.put(SocialType.KAKAO, kakaoSocialService);
        socialServiceMap.put(SocialType.APPLE, appleSocialService);
    }

    public SocialService getSocialService(SocialType socialType) {
        return socialServiceMap.get(socialType);
    }
}
