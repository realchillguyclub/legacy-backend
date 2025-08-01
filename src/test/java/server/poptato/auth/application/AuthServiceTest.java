package server.poptato.auth.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.auth.application.response.LoginResponseDto;
import server.poptato.auth.application.service.AuthService;
import server.poptato.auth.application.service.JwtService;
import server.poptato.external.oauth.SocialService;
import server.poptato.external.oauth.SocialServiceProvider;
import server.poptato.external.oauth.SocialUserInfo;
import server.poptato.global.dto.TokenPair;
import server.poptato.user.application.event.CreateUserEvent;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.UserRepository;
import server.poptato.user.domain.value.MobileType;
import server.poptato.user.domain.value.SocialType;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    SocialServiceProvider socialServiceProvider;

    @Mock
    SocialService socialService;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtService jwtService;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    @ParameterizedTest
    @MethodSource("소셜_종류별")
    @DisplayName("[SCN-AUTH-001][TC-LOGIN-001]")
    void login_새로운_유저_로그인_성공(LoginRequestDto requestDto,SocialUserInfo userInfo) {
        //given
        Long userId = 1L;
        Mockito.when(socialServiceProvider.getSocialService(requestDto.socialType())).thenReturn(socialService);
        Mockito.when(socialService.getUserData(requestDto)).thenReturn(userInfo);
        Mockito.when(userRepository.findBySocialId(userInfo.socialId())).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User newUser = invocation.getArgument(0);
                    ReflectionTestUtils.setField(newUser, "id", userId);
                    return newUser;
                });

        long userCount = 1L;
        Mockito.when(userRepository.count()).thenReturn(userCount);

        ArgumentCaptor<CreateUserEvent> createUserEventCaptor = ArgumentCaptor.forClass(CreateUserEvent.class);
        Mockito.doNothing().when(eventPublisher).publishEvent(createUserEventCaptor.capture());

        // when
        TokenPair tokenPair = new TokenPair("mockAccessToken", "mockRefreshToken");
        Mockito.when(jwtService.generateTokenPair(String.valueOf(userId)))
                .thenReturn(tokenPair);
        LoginResponseDto responseDto = authService.login(requestDto);

        // then
        Mockito.verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        Assertions.assertThat(responseDto.isNewUser()).isTrue();
        Assertions.assertThat(responseDto.userId()).isEqualTo(savedUser.getId());

        Mockito.verify(socialServiceProvider).getSocialService(requestDto.socialType());
        Mockito.verify(socialService).getUserData(requestDto);
        Mockito.verify(userRepository).findBySocialId(userInfo.socialId());
    }


    private static Stream<Arguments> 소셜_종류별() {
        return Stream.of(
                Arguments.of(
                        new LoginRequestDto(SocialType.KAKAO, "access-token", MobileType.ANDROID, "client-id", null, null),
                        new SocialUserInfo("social-id", "테스터", "test@test.com", "https://image.com")
                ),
                Arguments.of(
                        new LoginRequestDto(SocialType.APPLE, "access-token", MobileType.IOS, "client-id", "테스터", "test@test.com"),
                        new SocialUserInfo("social-id", "테스터", "test@test.com", "https://image.com")
                )
        );
    }

    @Test
    @DisplayName("[SCN-AUTH-001][TC-LOGIN-002]")
    void login_새로운_APPLE_유저_로그인_실패() {

    }

    @Test
    @DisplayName("이미지 업데이트, FCM 토큰 저장")
    void login_존재하는_유저_로그인_성공() {

    }

}
