package server.poptato.external.firebase.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() throws IOException {
        String firebaseKeyPath = "ildan-project-firebase-adminsdk-rcb58-766d3dd75f.json";

        if (!Files.exists(Paths.get(firebaseKeyPath))) {
            throw new IllegalArgumentException("Firebase key file not found: " + firebaseKeyPath);
        }

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(firebaseKeyPath)))
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}
