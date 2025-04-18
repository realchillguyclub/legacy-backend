package server.poptato.external.firebase.application;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.user.domain.entity.Mobile;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.MobileRepository;
import server.poptato.user.domain.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmNotificationService {
    private final FcmService fcmService;
    private final FcmTokenService fcmTokenService;
    private final UserRepository userRepository;
    private final MobileRepository mobileRepository;
    private final TodoRepository todoRepository;

    /**
     * 마감기한 푸쉬알림을 전체 유저에게 전송합니다.
     */
    @Async
    public void sendDeadlineNotifications() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (Boolean.TRUE.equals(user.getIsPushAlarm())) {
                sendUserDeadlineNotification(user);
            }
        }
    }

    /**
     * 특정 유저에게 마감기한 푸쉬알림을 전송합니다.
     */
    private void sendUserDeadlineNotification(User user) {
        List<Todo> todosDueToday = todoRepository.findTodosDueToday(user.getId(), LocalDate.now());
        if (!todosDueToday.isEmpty()) {
            List<Mobile> mobiles = mobileRepository.findAllByUserId(user.getId());
            for (Mobile mobile : mobiles) {
                for (Todo todo : todosDueToday) {
                    sendPushOrDeleteToken(mobile.getClientId(), todo.getContent());
                }
            }
        }
    }

    /**
     * 마감기한 푸쉬알림을 전송하거나 유효하지 않은 FCM 토큰을 삭제합니다.
     */
    private void sendPushOrDeleteToken(String clientId, String todoContent) {
        try {
            fcmService.sendPushNotification(clientId, "오늘 마감 예정인 할 일", todoContent);
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT ||
                    e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                fcmTokenService.deleteInvalidToken(clientId);
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
