package server.poptato.external.oauth.kakao;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.external.oauth.SocialService;
import server.poptato.external.oauth.SocialUserInfo;

@Service
@RequiredArgsConstructor
public class KakaoSocialService extends SocialService {
    private static final String Bearer = "Bearer ";
    private final KakaoApiClient kakaoApiClient;

    @Override
    public SocialUserInfo getUserData(LoginRequestDto request) {
        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation(Bearer + request.accessToken());

        return new SocialUserInfo(
                String.valueOf(userResponse.id()),
                userResponse.properties().nickname(),
                userResponse.kakao_account().email(),
                userResponse.kakao_account().profile().profile_image_url()
        );
    }
}
