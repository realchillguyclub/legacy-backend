package server.poptato.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.auth.api.request.ReissueTokenRequestDto;
import server.poptato.auth.application.response.LoginResponseDto;
import server.poptato.auth.status.AuthErrorStatus;
import server.poptato.external.oauth.SocialService;
import server.poptato.external.oauth.SocialServiceProvider;
import server.poptato.external.oauth.SocialUserInfo;
import server.poptato.global.dto.TokenPair;
import server.poptato.global.exception.CustomException;
import server.poptato.todo.constant.TutorialMessage;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.user.domain.entity.Mobile;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.MobileRepository;
import server.poptato.user.domain.repository.UserRepository;
import server.poptato.user.domain.value.SocialType;
import server.poptato.user.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final SocialServiceProvider socialServiceProvider;
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final TodoRepository todoRepository;
    private final MobileRepository mobileRepository;

    /**
     * 소셜 로그인 처리 메서드.
     * 소셜 인증 정보를 기반으로 로그인 처리를 수행하며, 신규 유저일 경우 데이터를 저장합니다.
     * 또한, FCM 토큰을 저장하거나 업데이트합니다.
     *
     * @param request 사용자의 로그인 요청 정보 (소셜 타입, 액세스 토큰 등)
     * @return 로그인 결과로 생성된 액세스 토큰, 리프레시 토큰, 유저 ID, 신규 유저 여부
     */
    public LoginResponseDto login(final LoginRequestDto request) {
        SocialService socialService = socialServiceProvider.getSocialService(request.socialType());
        SocialUserInfo userInfo = socialService.getUserData(request);
        Optional<User> findUser = userRepository.findBySocialId(userInfo.socialId());
        if (findUser.isEmpty()) {
            User newUser = saveNewData(request, userInfo);
            saveOrUpdateFcmToken(newUser.getId(), request);
            return createLoginResponse(newUser.getId(), true);
        }
        updateImage(findUser.get(), userInfo);
        saveOrUpdateFcmToken(findUser.get().getId(), request);
        return createLoginResponse(findUser.get().getId(), false);
    }

    /**
     * FCM 토큰 저장 및 업데이트 메서드.
     * 기존에 저장된 FCM 토큰이 없을 경우 저장하며, 클라이언트 ID가 변경된 경우 업데이트합니다.
     *
     * @param userId 유저 ID
     * @param request 로그인 요청 정보
     */
    private void saveOrUpdateFcmToken(Long userId, LoginRequestDto request) {
        Optional<Mobile> existingMobile = mobileRepository.findByUserId(userId);
        if (existingMobile.isEmpty()) {
            Mobile newMobile = Mobile.create(request, userId);
            mobileRepository.save(newMobile);
        } else {
            Mobile mobile = existingMobile.get();
            if (!mobile.getClientId().equals(request.clientId())) {
                mobile.setClientId(request.clientId());
                mobile.setModifyDate(LocalDateTime.now());
                mobileRepository.save(mobile);
            }
        }
    }

    /**
     * 신규 유저 데이터 저장 메서드.
     * - Apple 로그인인 경우, name 값이 없으면 예외 발생 (Apple은 최초 로그인 시에만 name 제공).
     * - 유저 정보를 생성하고, 기본 튜토리얼 데이터를 추가로 저장.
     *
     * @param request 로그인 요청 정보 (프론트에서 받은 name, email 포함)
     * @param userInfo 소셜 유저 정보 (sub 값 포함)
     * @return 생성된 유저 객체
     * @throws CustomException Apple 로그인인데 name 값이 없는 경우 예외 발생
     */
    private User saveNewData(LoginRequestDto request, SocialUserInfo userInfo) {
        if (request.socialType() == SocialType.APPLE && request.name() == null) {
            // Apple 신규 유저의 name 값이 null인 경우 예외 처리
            throw new CustomException(AuthErrorStatus._HAS_NOT_NEW_APPLE_USER_NAME);
        }
        String imageUrl = getHttpsUrl(userInfo.imageUrl());
        User user = User.create(request, userInfo, imageUrl);
        User newUser = userRepository.save(user);
        Todo tutorialTodo = Todo.createBacklog(newUser.getId(), TutorialMessage.GUIDE, 1);
        todoRepository.save(tutorialTodo);
        return newUser;
    }

    /**
     * HTTP 이미지를 HTTPS 이미지로 변환하는 메서드.
     * 이미지 URL이 HTTP로 시작하면 HTTPS로 변환합니다.
     *
     * @param imageUrl 소셜 유저 이미지
     * @return 변환된 이미지 URL
     */
    private static String getHttpsUrl(String imageUrl) {
        if (imageUrl != null && imageUrl.startsWith("http://")) {
            imageUrl = imageUrl.replaceFirst("http://", "https://");
        }
        return imageUrl;
    }

    /**
     * 유저 프로필 이미지 업데이트 메서드.
     * - 기존 유저의 프로필 이미지가 변경된 경우 업데이트함.
     *
     * @param existingUser 기존 유저 객체
     * @param userInfo 소셜 유저 정보 (imageUrl 포함)
     */
    private void updateImage(User existingUser, SocialUserInfo userInfo) {
        String imageUrl = getHttpsUrl(userInfo.imageUrl());
        if (existingUser.getImageUrl() == null || !existingUser.getImageUrl().equals(imageUrl)) {
            existingUser.updateImageUrl(imageUrl);
            userRepository.save(existingUser);
        }
    }

    /**
     * 로그인 응답 데이터 생성 메서드.
     * 유저 ID와 신규 유저 여부를 기반으로 토큰 페어와 함께 응답 데이터를 생성합니다.
     *
     * @param userId 유저 ID
     * @param isNewUser 신규 유저 여부
     * @return 로그인 응답 데이터
     */
    private LoginResponseDto createLoginResponse(Long userId, boolean isNewUser) {
        TokenPair tokenPair = jwtService.generateTokenPair(String.valueOf(userId));
        return LoginResponseDto.of(tokenPair.accessToken(), tokenPair.refreshToken(), isNewUser, userId);
    }

    /**
     * 로그아웃 처리 메서드.
     * 유저의 리프레시 토큰을 삭제하여 로그아웃을 처리합니다.
     *
     * @param userId 로그아웃할 유저 ID
     */
    public void logout(final Long userId) {
        userValidator.checkIsExistUser(userId);
        jwtService.deleteRefreshToken(String.valueOf(userId));
    }

    /**
     * 토큰 갱신 메서드.
     * 유효한 리프레시 토큰을 기반으로 새로운 토큰 페어를 생성하고 저장합니다.
     *
     * @param reissueTokenRequestDto 토큰 갱신 요청 정보
     * @return 새로운 토큰 페어
     */
    public TokenPair refresh(final ReissueTokenRequestDto reissueTokenRequestDto) {
        checkIsValidToken(reissueTokenRequestDto.refreshToken());

        final String userId = jwtService.getUserIdInToken(reissueTokenRequestDto.refreshToken());
        userValidator.checkIsExistUser(Long.parseLong(userId));

        final TokenPair tokenPair = jwtService.generateTokenPair(userId);
        jwtService.saveRefreshToken(userId, tokenPair.refreshToken());

        return tokenPair;
    }

    /**
     * 리프레시 토큰 검증 메서드.
     * 토큰의 유효성을 확인하고 저장된 토큰과 비교합니다.
     *
     * @param refreshToken 검증할 리프레시 토큰
     * @throws RuntimeException 토큰이 유효하지 않을 경우 예외 발생
     */
    private void checkIsValidToken(String refreshToken) {
        try {
            jwtService.verifyRefreshToken(refreshToken);
            jwtService.compareRefreshToken(jwtService.getUserIdInToken(refreshToken), refreshToken);
        } catch (Exception e) {
            throw e;
        }
    }
}
