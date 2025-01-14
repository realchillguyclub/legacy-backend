package server.poptato.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import server.poptato.auth.api.request.LoginRequestDto;
import server.poptato.auth.api.request.ReissueTokenRequestDto;
import server.poptato.auth.application.response.LoginResponseDto;
import server.poptato.auth.converter.AuthDtoConverter;
import server.poptato.external.oauth.SocialService;
import server.poptato.external.oauth.SocialServiceProvider;
import server.poptato.external.oauth.SocialUserInfo;
import server.poptato.global.dto.TokenPair;
import server.poptato.todo.constant.TutorialMessage;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.user.domain.entity.Mobile;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.MobileRepository;
import server.poptato.user.domain.repository.UserRepository;
import server.poptato.user.domain.value.MobileType;
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

    public LoginResponseDto login(final LoginRequestDto loginRequestDto) {
        SocialService socialService = socialServiceProvider.getSocialService(loginRequestDto.socialType());
        SocialUserInfo userInfo = socialService.getUserData(loginRequestDto.accessToken());
        Optional<User> findUser = userRepository.findBySocialId(userInfo.socialId());
        if (findUser.isEmpty()) {
            User newUser = saveNewDatas(loginRequestDto, userInfo);
            saveOrUpdateFcmToken(newUser.getId(), loginRequestDto);
            return createLoginResponse(newUser.getId(), true);
        }
        updateImage(findUser.get(),userInfo);
        saveOrUpdateFcmToken(findUser.get().getId(), loginRequestDto);
        return createLoginResponse(findUser.get().getId(), false);
    }
    private void saveOrUpdateFcmToken(Long userId, LoginRequestDto requestDto) {

        Optional<Mobile> existingMobile = mobileRepository.findByUserId(userId);
        if (existingMobile.isEmpty()) {
            Mobile newMobile = Mobile.create(requestDto, userId);
            mobileRepository.save(newMobile);
        } else {
            Mobile mobile = existingMobile.get();
            if (!mobile.getClientId().equals(requestDto.clientId())) {
                mobile.setClientId(requestDto.clientId());
                mobile.setModifyDate(LocalDateTime.now());
                mobileRepository.save(mobile);
            }
        }
    }

    private User saveNewDatas(LoginRequestDto requestDto, SocialUserInfo userInfo) {
        String imageUrl = getHttpsUrl(userInfo);
        User user = User.create(requestDto, userInfo, imageUrl);
        User newUser = userRepository.save(user);
        Todo turorialTodo = Todo.createBacklog(newUser.getId(), TutorialMessage.GUIDE, 1);
        todoRepository.save(turorialTodo);
        return newUser;
    }

    private static String getHttpsUrl(SocialUserInfo userInfo) {
        String imageUrl = userInfo.imageUrl();

        if (imageUrl != null && imageUrl.startsWith("http://")) {
            imageUrl = imageUrl.replaceFirst("http://", "https://");
        }
        return imageUrl;
    }

    private void updateImage(User existingUser, SocialUserInfo userInfo) {
        String imageUrl = getHttpsUrl(userInfo);

        if (existingUser.getImageUrl() == null || !existingUser.getImageUrl().equals(imageUrl)) {
            existingUser.updateImageUrl(imageUrl);
            userRepository.save(existingUser);
        }
    }

    private LoginResponseDto createLoginResponse(Long userId, boolean isNewUser) {
        TokenPair tokenPair = jwtService.generateTokenPair(String.valueOf(userId));
        return AuthDtoConverter.toLoginDto(tokenPair, userId, isNewUser);
    }

    public void logout(final Long userId) {
        userValidator.checkIsExistUser(userId);
        jwtService.deleteRefreshToken(String.valueOf(userId));
    }

    public TokenPair refresh(final ReissueTokenRequestDto reissueTokenRequestDto) {
        checkIsValidToken(reissueTokenRequestDto.getRefreshToken());

        final String userId = jwtService.getUserIdInToken(reissueTokenRequestDto.getRefreshToken());
        userValidator.checkIsExistUser(Long.parseLong(userId));

        final TokenPair tokenPair = jwtService.generateTokenPair(userId);
        jwtService.saveRefreshToken(userId, tokenPair.refreshToken());

        return tokenPair;
    }

    private void checkIsValidToken(String refreshToken) {
        try {
            jwtService.verifyToken(refreshToken);
            jwtService.compareRefreshToken(jwtService.getUserIdInToken(refreshToken), refreshToken);
        } catch (Exception e) {
            throw e;
        }
    }
}
