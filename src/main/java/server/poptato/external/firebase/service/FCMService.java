package server.poptato.external.firebase.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    /**
     * 푸시 알림을 보내는 메서드.
     * firebase에 푸시 알림 요청을 보낸다. 예외 발생 시 호출하는 메서드로 FirebaseMessagingException 예외를 던진다
     * @param userToken
     * @param title
     * @param message
     * @throws FirebaseMessagingException
     */
    public void sendPushNotification(String userToken, String title, String message) throws FirebaseMessagingException {
        Message firebaseMessage = Message.builder()
                .setToken(userToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(message)
                        .build())
                .build();

        FirebaseMessaging.getInstance().send(firebaseMessage);
    }
}
