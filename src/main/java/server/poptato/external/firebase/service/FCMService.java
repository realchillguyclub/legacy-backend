package server.poptato.external.firebase.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    public void sendPushNotification(String userToken, String title, String message) {
        try {
            Message firebaseMessage = Message.builder()
                    .setToken(userToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(message)
                            .build())
                    .build();

            FirebaseMessaging.getInstance().send(firebaseMessage);
        } catch (
                FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}
