package server.poptato.external.firebase.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmNotificationScheduler {
    private final FcmNotificationBatchService fcmNotificationBatchService;
    private final FcmTokenService fcmTokenService;

    /**
     * 비활성 FCM 토큰을 삭제한다.
     * - 기준: 최근 1개월간 사용되지 않은 토큰
     */
    @Scheduled(cron = "${scheduling.fcmCleanupCron}")
    public void deleteOldFcmTokens() {
        fcmTokenService.deleteOldFcmTokens();
    }

    /**
     * 하루 시작 푸쉬알림을 전송한다.
     * - 대상: 푸쉬 알림을 허용한 전체 유저
     */
    @Scheduled(cron = "${scheduling.startNotificationCron}")
    @Async
    public void sendStartNotifications() {
        fcmNotificationBatchService.sendStartNotifications();
    }

    /**
     * 일과 정리 푸쉬알림을 전송한다.
     * - 대상: 미완료 오늘 할 일이 존재하는 유저
     */
    @Scheduled(cron = "${scheduling.endOfDayNotificationCron}")
    @Async
    public void sendEndOfDayNotifications() {
        fcmNotificationBatchService.sendEndOfDayNotifications();
    }

    /**
     * 마감기한 푸쉬알림을 전송한다.
     * - 대상: 오늘 마감인 할 일을 보유한 유저
     */
    @Scheduled(cron = "${scheduling.deadlineNotificationCron}")
    @Async
    public void sendDeadlineNotifications() {
        fcmNotificationBatchService.sendDeadlineNotifications();
    }

    /**
     * 설정한 시간 1시간 전에 푸쉬알림을 전송한다.
     * - 대상: 시간이 설정된 오늘 할 일을 보유한 유저
     */
    @Scheduled(cron = "${scheduling.timeDeadlineNotificationCron}")
    @Async
    public void sendTimeDeadlineNotification() {
        fcmNotificationBatchService.sendTimeDeadlineNotifications();
    }
}
