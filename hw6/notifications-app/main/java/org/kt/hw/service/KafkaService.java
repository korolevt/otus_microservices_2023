package org.kt.hw.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.Notification;
import org.kt.hw.model.NotificationDTO;
import org.kt.hw.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
//@Scope("singleton")
public class KafkaService {
    private final NotificationRepository notificationRepository;

    @KafkaListener(id="id", topics = "notifications")
    public void listen(String message) {
        try {
            log.debug("Received Message: " + message);

            NotificationDTO data = new Gson().fromJson(message, NotificationDTO.class);

            Notification newNotification = NotificationRepository.createNotification(data.getUserId(), data.getText());
            notificationRepository.save(newNotification);
            log.debug("Save notification: " + newNotification);
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }

/*
    @KafkaListener(id="id", topics = "notifications")
    public void listen(String message) {
        try {
            log.debug("Received Message: " + message);
            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

            Notification newNotification =
                    NotificationRepository.createNotification(
                            jsonObject.get("userId").getAsInt(),
                            jsonObject.get("text").getAsString());
            notificationRepository.save(newNotification);
            log.debug("Save notification: " + newNotification);
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }
 */
}
