package server.poptato.infra.oauth;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.poptato.infra.oauth.apple.AppleSocialService;
import server.poptato.infra.oauth.kakao.KakaoSocialService;
import server.poptato.user.domain.value.SocialType;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SocialServiceProvider {

    private static final Map<SocialType, SocialService> socialServiceMap = new EnumMap<>(SocialType.class);
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
