package server.poptato.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.auth.application.service.JwtService;
import server.poptato.user.domain.entity.DeleteReason;
import server.poptato.user.domain.repository.DeleteReasonRepository;
import server.poptato.user.domain.repository.MobileRepository;
import server.poptato.user.domain.value.Reason;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.user.api.request.UserDeleteRequestDTO;
import server.poptato.user.application.response.UserInfoResponseDto;
import server.poptato.user.converter.UserDtoConverter;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.UserRepository;
import server.poptato.user.validator.UserValidator;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final JwtService jwtService;
    private final UserValidator userValidator;
    private final DeleteReasonRepository deleteReasonRepository;
    private final MobileRepository mobileRepository;

    public void deleteUser(Long userId, UserDeleteRequestDTO userDeleteRequestDTO) {
        User user = userValidator.checkIsExistAndReturnUser(userId);
        saveDeleteReasons(userId, userDeleteRequestDTO.reasons(), userDeleteRequestDTO.userInputReason());
        todoRepository.deleteAllByUserId(userId);
        mobileRepository.deleteAllByUserId(userId);
        jwtService.deleteRefreshToken(String.valueOf(userId));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserInfo(Long userId) {
        User user = userValidator.checkIsExistAndReturnUser(userId);
        return UserDtoConverter.toUserInfoDto(user);
    }
    private void saveDeleteReasons(Long userId, List<Reason> reasons, String userInputReason) {
        if (reasons != null && !reasons.isEmpty()) {
            reasons.forEach(reason -> {
                DeleteReason deleteReason = DeleteReason.builder()
                        .userId(userId)
                        .deleteReason(reason.getValue())
                        .build();
                deleteReasonRepository.save(deleteReason);
            });
        }
        if (userInputReason != null && !userInputReason.trim().isEmpty()) {
            DeleteReason deleteReason = DeleteReason.builder()
                    .userId(userId)
                    .deleteReason(userInputReason)
                    .build();
            deleteReasonRepository.save(deleteReason);
        }
    }
}
