package server.poptato.auth.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
import org.springframework.transaction.annotation.Transactional;
import server.poptato.auth.api.request.FCMTokenRequestDto;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.auth.api.request.ReissueTokenRequestDto;
import server.poptato.auth.application.response.LoginResponseDto;
import server.poptato.auth.application.service.AuthService;
import server.poptato.auth.application.service.JwtService;
import server.poptato.auth.status.AuthErrorStatus;
import server.poptato.external.oauth.SocialService;
import server.poptato.external.oauth.SocialServiceProvider;
import server.poptato.external.oauth.SocialUserInfo;
import server.poptato.global.dto.TokenPair;
import server.poptato.global.exception.CustomException;
import server.poptato.user.application.event.CreateUserEvent;
import server.poptato.user.domain.entity.Mobile;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.MobileRepository;
import server.poptato.user.domain.repository.UserRepository;
import server.poptato.user.domain.value.MobileType;
import server.poptato.user.domain.value.SocialType;
import server.poptato.user.status.MobileErrorStatus;
import server.poptato.user.validator.UserValidator;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    SocialServiceProvider socialServiceProvider;

    @Mock
    SocialService socialService;

    @Mock
    UserRepository userRepository;

    @Mock
    MobileRepository mobileRepository;

    @Mock
    UserValidator userValidator;

    @Mock
    JwtService jwtService;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    @ParameterizedTest
    @MethodSource("소셜_종류별")
    @DisplayName("[SCN-AUTH-001][TC-LOGIN-001] 신규 유저 소셜 로그인 시, 유저가 생성되어 저장되고 응답에 isNew=true 가 포함된다")
    void login_새로운_유저_로그인_성공(LoginRequestDto requestDto, SocialUserInfo userInfo) {
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
    @DisplayName("[SCN-AUTH-001][TC-LOGIN-002] 신규 유저 애플 로그인 시, name이 null 인 경우 AuthErrorStatus._HAS_NOT_NEW_APPLE_USER_NAME 예외가 발생한다")
    void login_새로운_APPLE_유저_로그인_실패() {
        //given
        LoginRequestDto requestDto = new LoginRequestDto(SocialType.APPLE, "access-token", MobileType.IOS, "client-id", null, "test@test.com");
        SocialUserInfo userInfo = new SocialUserInfo("social-id", "테스터", "test@test.com", "https://image.com");
        Long userId = 1L;
        Mockito.when(socialServiceProvider.getSocialService(requestDto.socialType())).thenReturn(socialService);
        Mockito.when(socialService.getUserData(requestDto)).thenReturn(userInfo);
        Mockito.when(userRepository.findBySocialId(userInfo.socialId())).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> authService.login(requestDto));

        // then
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(AuthErrorStatus._HAS_NOT_NEW_APPLE_USER_NAME);
    }

    @Test
    @DisplayName("[SCN-AUTH-001][TC-LOGIN-003] 기존 유저 소셜 로그인 시, 유저의 정보(이미지url, fcm토큰)가 업데이트 되고 응답에 isNew=false가 포함된다")
    void login_존재하는_유저_로그인_성공() {
        //given
        LoginRequestDto requestDto = new LoginRequestDto(SocialType.KAKAO, "access-token", MobileType.ANDROID, "client-id", null, null);
        SocialUserInfo userInfo = new SocialUserInfo("social-id", "테스터", "test@test.com", "https://image.com");
        Long userId = 1L;

        User existingUser = User.createUser(requestDto, userInfo, "https://image.com");
        ReflectionTestUtils.setField(existingUser, "id", userId);

        Mockito.when(socialServiceProvider.getSocialService(requestDto.socialType())).thenReturn(socialService);
        Mockito.when(socialService.getUserData(requestDto)).thenReturn(userInfo);
        Mockito.when(userRepository.findBySocialId(userInfo.socialId())).thenReturn(Optional.of(existingUser));
        Mockito.when(mobileRepository.findByClientId("client-id")).thenReturn(Optional.of(mock(Mobile.class))); // 중복
        Mockito.when(jwtService.generateTokenPair(String.valueOf(userId)))
                .thenReturn(new TokenPair("access-token", "refresh-token"));

        // when
        LoginResponseDto response = authService.login(requestDto);

        // then
        Assertions.assertThat(response.userId()).isEqualTo(userId);
        Assertions.assertThat(response.isNewUser()).isFalse();
        Assertions.assertThat(response.accessToken()).isEqualTo("access-token");
        Assertions.assertThat(response.refreshToken()).isEqualTo("refresh-token");

        Mockito.verify(userRepository).findBySocialId(userInfo.socialId());
        Mockito.verify(jwtService).generateTokenPair(String.valueOf(userId));
        Mockito.verify(mobileRepository).findByClientId("client-id");
        Mockito.verify(userRepository, Mockito.never()).save(existingUser); // image 안 바뀌면 저장 안 함
    }

    @Test
    @DisplayName("[SCN-AUTH-002][TC-LOGOUT-001] 로그아웃시 정상적으로 로그아웃된다")
    void logout_로그아웃_성공() {
        //given
        Long userId = 1L;
        FCMTokenRequestDto requestDto = new FCMTokenRequestDto("client-id");

        Mockito.doNothing().when(userValidator).checkIsExistUser(userId);
        Mockito.doNothing().when(mobileRepository).deleteByClientId(requestDto.clientId());
        Mockito.doNothing().when(jwtService).deleteRefreshToken(String.valueOf(userId));

        //when
        authService.logout(userId, requestDto);

        //then
        Mockito.verify(userValidator).checkIsExistUser(userId);
        Mockito.verify(mobileRepository).deleteByClientId(requestDto.clientId());
        Mockito.verify(jwtService).deleteRefreshToken(String.valueOf(userId));
    }

    @Test
    @DisplayName("[SCN-AUTH-003][TC-JWT-001] 유효한 리프레시 토큰을 기반으로 새로운 토큰 페어를 생성하여 반환한다")
    void refresh_jwt_토큰_갱신() {
        //given
        Long userId = 1L;
        ReissueTokenRequestDto requestDto = new ReissueTokenRequestDto("access-token", "refresh-token", "client-id");
        TokenPair tokenPair = new TokenPair("access-token", "refresh-token");

        Mockito.doNothing().when(jwtService).verifyRefreshToken(requestDto.refreshToken());
        Mockito.when(jwtService.getUserIdInToken(requestDto.refreshToken())).thenReturn(String.valueOf(userId));
        Mockito.doNothing().when(jwtService).compareRefreshToken(String.valueOf(userId), requestDto.refreshToken());

        Mockito.doNothing().when(userValidator).checkIsExistUser(userId);

        Mockito.when(jwtService.generateTokenPair(String.valueOf(userId))).thenReturn(tokenPair);
        Mockito.doNothing().when(jwtService).saveRefreshToken(String.valueOf(userId), tokenPair.refreshToken());

        //when
        TokenPair result = authService.refresh(requestDto);

        // then
        assertEquals(tokenPair.accessToken(), result.accessToken());
        assertEquals(tokenPair.refreshToken(), result.refreshToken());

        Mockito.verify(jwtService).verifyRefreshToken(requestDto.refreshToken());
        Mockito.verify(jwtService).getUserIdInToken(requestDto.refreshToken());
        Mockito.verify(jwtService).compareRefreshToken(String.valueOf(userId), requestDto.refreshToken());
        Mockito.verify(userValidator).checkIsExistUser(userId);
        Mockito.verify(jwtService).generateTokenPair(String.valueOf(userId));
        Mockito.verify(jwtService).saveRefreshToken(String.valueOf(userId), tokenPair.refreshToken());
    }

    @Test
    @DisplayName("[SCN-AUTH-004][TC-FCM-001] 존재하는 fcm토큰일 경우 접속한 날짜로 수정일을 변경한다")
    void refreshFCMToken_존재하는_fcm토큰_정상_실행(){
        // given
        String clientId = "client-id";
        Mobile mockMobile = Mockito.mock(Mobile.class);
        Mockito.when(mobileRepository.findByClientId(clientId)).thenReturn(Optional.of(mockMobile));
        Mockito.doNothing().when(mockMobile).updateModifiedTime();

        // when
        authService.refreshFCMToken(clientId);

        // then
        Mockito.verify(mobileRepository).findByClientId(clientId);
        Mockito.verify(mockMobile).updateModifiedTime();
    }

    @Test
    @DisplayName("[SCN-AUTH-004][TC-FCM-002] 존재하지 않는 fcm토큰일 경우 예외가 발생한다")
    void refreshFCMToken_존재하지_않는_fcm토큰_예외발생(){
        // given
        String clientId = "client-id";
        Mockito.when(mobileRepository.findByClientId(clientId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                authService.refreshFCMToken(clientId));

        assertEquals(MobileErrorStatus._NOT_EXIST_FCM_TOKEN, exception.getErrorCode());

        Mockito.verify(mobileRepository).findByClientId(clientId);
    }
}
