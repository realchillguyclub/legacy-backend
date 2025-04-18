package server.poptato.external.firebase.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmNotificationScheduler {
    private final FcmNotificationService fcmNotificationService;
    private final FcmTokenService fcmTokenService;

    /**
     * 비활성 FCM 토큰을 삭제한다.
     */
    @Scheduled(cron = "${scheduling.fcmCleanupCron}")
    public void deleteOldFcmTokens() {
        fcmTokenService.deleteOldFcmTokens();
    }

    /**
     * 마감일 알림을 전송한다.
     */
    @Scheduled(cron = "${scheduling.deadlineNotificationCron}")
    @Async
    public void sendDeadlineNotifications() {
        fcmNotificationService.sendDeadlineNotifications();
    }
}
