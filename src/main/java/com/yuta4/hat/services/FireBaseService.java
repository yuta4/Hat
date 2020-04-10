package com.yuta4.hat.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class FireBaseService {

    Logger logger = LoggerFactory.getLogger(FireBaseService.class);

    @Value("${firebase.project.url}")
    private String fireBaseProjectUrl;

    @PostConstruct
    public void initialize() {
        try {
            if(FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .setDatabaseUrl(fireBaseProjectUrl)
                        .build();
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            logger.error("Check GOOGLE_APPLICATION_CREDENTIALS env variable");
        }
    }

    public void send(String topic, Map<String, String> data) throws FirebaseMessagingException {
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        Message message = Message.builder()
                .setTopic(topic)
                .putAllData(data)
                .build();
        logger.trace("Sending FCM topic : {}, data : {}", topic, data);
        firebaseMessaging.send(message);
    }
}
