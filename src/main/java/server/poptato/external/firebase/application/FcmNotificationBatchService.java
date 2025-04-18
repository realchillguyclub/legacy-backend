package server.poptato.external.firebase.application;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import server.poptato.external.firebase.template.FcmNotificationTemplate;
import server.poptato.global.util.BatchUtil;
import server.poptato.todo.domain.entity.Todo;
import server.poptato.todo.domain.repository.TodoRepository;
import server.poptato.todo.domain.value.TodayStatus;
import server.poptato.todo.domain.value.Type;
import server.poptato.user.domain.entity.Mobile;
import server.poptato.user.domain.entity.User;
import server.poptato.user.domain.repository.MobileRepository;
import server.poptato.user.domain.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmNotificationBatchService {
    private final FcmService fcmService;
    private final FcmTokenService fcmTokenService;
    private final UserRepository userRepository;
    private final MobileRepository mobileRepository;
    private final TodoRepository todoRepository;

    @Value("${batch.size}")
    private int batchSize;

    /**
     * 마감기한 푸쉬알림을 전체 유저에게 전송한다.
     */
    @Async
    public void sendDeadlineNotifications() {
        List<User> users = userRepository.findAll();
        BatchUtil.splitIntoBatches(users, batchSize).forEach(batch -> {
            for (User user : batch) {
                if (Boolean.TRUE.equals(user.getIsPushAlarm())) {
                    sendUserDeadlineNotification(user);
                }
            }
        });
    }

    /**
     * 특정 유저에게 마감기한 푸쉬알림을 전송한다.
     *
     * @param user 알림을 보낼 대상 유저
     */
    private void sendUserDeadlineNotification(User user) {
        List<Todo> todosDueToday = todoRepository.findTodosDueToday(user.getId(), LocalDate.now());
        if (!todosDueToday.isEmpty()) {
            List<Mobile> mobiles = mobileRepository.findAllByUserId(user.getId());
            for (Mobile mobile : mobiles) {
                for (Todo todo : todosDueToday) {
                    String body = String.format(FcmNotificationTemplate.DEADLINE.getBody(), todo.getContent());
                    sendPushOrDeleteToken(
                            mobile.getClientId(),
                            FcmNotificationTemplate.DEADLINE.getTitle(),
                            body
                    );
                }
            }
        }
    }

    /**
     * 하루 시작 푸쉬알림을 전체 유저에게 전송한다.
     */
    @Async
    public void sendStartNotifications() {
        List<User> users = userRepository.findAll();
        BatchUtil.splitIntoBatches(users, batchSize).forEach(batch -> {
            for (User user : batch) {
                if (Boolean.TRUE.equals(user.getIsPushAlarm())) {
                    List<Mobile> mobiles = mobileRepository.findAllByUserId(user.getId());
                    for (Mobile mobile : mobiles) {
                        sendPushOrDeleteToken(
                                mobile.getClientId(),
                                FcmNotificationTemplate.START_OF_DAY.getTitle(),
                                FcmNotificationTemplate.START_OF_DAY.getBody()
                        );
                    }
                }
            }
        });
    }

    /**
     * 일과 정리 푸쉬알림을 전체 유저에게 전송한다.
     */
    @Async
    public void sendEndOfDayNotifications() {
        List<User> users = userRepository.findAll();
        BatchUtil.splitIntoBatches(users, batchSize).forEach(batch -> {
            for (User user : batch) {
                if (Boolean.TRUE.equals(user.getIsPushAlarm())) {
                    boolean hasIncompleteTodayTodos =
                            todoRepository.existsByUserIdAndTypeAndTodayStatus(user.getId(), Type.TODAY, TodayStatus.INCOMPLETE);
                    if (hasIncompleteTodayTodos) {
                        List<Mobile> mobiles = mobileRepository.findAllByUserId(user.getId());
                        for (Mobile mobile : mobiles) {
                            sendPushOrDeleteToken(
                                    mobile.getClientId(),
                                    FcmNotificationTemplate.END_OF_DAY.getTitle(),
                                    FcmNotificationTemplate.END_OF_DAY.getBody()
                            );
                        }
                    }
                }
            }
        });
    }

    /**
     * FCM 푸쉬알림을 전송하거나, 유효하지 않은 토큰인 경우 삭제한다.
     *
     * @param clientId FCM 토큰 (디바이스 식별자)
     * @param title    알림 제목
     * @param body     알림 본문
     */
    private void sendPushOrDeleteToken(String clientId, String title, String body) {
        try {
            fcmService.sendPushNotification(clientId, title, body);
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
