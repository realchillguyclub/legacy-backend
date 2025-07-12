package server.poptato.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.poptato.auth.application.service.JwtService;
import server.poptato.category.domain.repository.CategoryRepository;
import server.poptato.user.api.request.UserCommentRequestDTO;
import server.poptato.user.application.event.CreateUserCommentEvent;
import server.poptato.user.domain.entity.Comment;
import server.poptato.user.domain.entity.DeleteReason;
import server.poptato.user.domain.repository.CommentRepository;
import server.poptato.user.domain.repository.DeleteReasonRepository;
import server.poptato.user.domain.value.Reason;
import server.poptato.user.api.request.UserDeleteRequestDTO;
import server.poptato.user.application.response.UserInfoResponseDto;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.UserRepository;
import server.poptato.user.validator.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtService jwtService;
    private final UserValidator userValidator;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final DeleteReasonRepository deleteReasonRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;

    /**
     * 사용자 탈퇴 처리 메서드.
     *
     * 주어진 사용자 ID를 기반으로 탈퇴 요청을 처리합니다.
     * 탈퇴 이유를 저장하고, 관련 데이터(할 일, 모바일 정보)를 삭제한 뒤 사용자를 삭제합니다.
     *
     * @param userId 사용자 ID
     * @param userDeleteRequestDTO 탈퇴 요청 데이터
     */
    @Transactional
    public void deleteUser(Long userId, UserDeleteRequestDTO userDeleteRequestDTO) {
        User user = userValidator.checkIsExistAndReturnUser(userId);
        saveDeleteReasons(userId, userDeleteRequestDTO.reasons(), userDeleteRequestDTO.userInputReason());
        userRepository.delete(user);
        categoryRepository.deleteByUserId(userId);
        jwtService.deleteRefreshToken(String.valueOf(userId));
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

    /**
     * 사용자 정보 조회 메서드.
     *
     * 주어진 사용자 ID를 기반으로 사용자의 이름, 이메일, 이미지 URL 정보를 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보 DTO
     */
    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserInfo(Long userId) {
        User user = userValidator.checkIsExistAndReturnUser(userId);
        return UserInfoResponseDto.of(user);
    }

    /**
     * 사용자 의견 생성 메서드.
     *
     * @param userId 사용자 ID
     * @param requestDTO 의견 정보
     */
    @Transactional
    public void createAndSendUserComment(Long userId, UserCommentRequestDTO requestDTO) {
        User user = userValidator.checkIsExistAndReturnUser(userId);
        Comment comment = commentRepository.save(requestDTO.toEntity(user.getId()));

        eventPublisher.publishEvent(CreateUserCommentEvent.from(comment, user.getName()));
    }
}
