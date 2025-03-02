package server.poptato.external.firebase.service;

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
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmNotificationService {
    private final FCMService fcmService;
    private final UserRepository userRepository;
    private final MobileRepository mobileRepository;
    private final TodoRepository todoRepository;

    /**
     * 비활성 FCM 토큰을 삭제한다.
     */
    public void deleteOldFcmTokens() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        mobileRepository.deleteOldTokens(oneMonthAgo);
    }

    /**
     * 마감일 알림을 전송한다.
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
     * 특정 유저에게 마감일 알림을 전송한다.
     */
    private void sendUserDeadlineNotification(User user) {
        List<Todo> todosDueToday = todoRepository.findTodosDueToday(user.getId(), LocalDate.now());
        if (!todosDueToday.isEmpty()) {
            List<Mobile> mobiles = mobileRepository.findAllByUserId(user.getId());
            for (Mobile mobile : mobiles) {
                for (Todo todo : todosDueToday) {
                    sendPushNotificationOrDeleteFcmToken(mobile.getClientId(), todo.getContent());
                }
            }
        }
    }

    /**
     * 푸시 알림을 전송하거나, 유효하지 않은 FCM 토큰을 삭제한다.
     */
    private void sendPushNotificationOrDeleteFcmToken(String clientId, String todoContent) {
        try {
            fcmService.sendPushNotification(clientId, "오늘 마감 예정인 할 일", todoContent);
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode().equals(MessagingErrorCode.INVALID_ARGUMENT) ||
                    e.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                mobileRepository.deleteByClientId(clientId);
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
