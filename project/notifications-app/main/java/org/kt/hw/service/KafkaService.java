package org.kt.hw.service;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.Notification;
import org.kt.hw.model.NotificationKafka;
import org.kt.hw.repository.NotificationRepository;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.Instant;

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

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantDeserializer())
                    .create();

            NotificationKafka data = gson.fromJson(message, NotificationKafka.class);

            Notification newNotification = NotificationRepository.createNotification(data.getUserId(), data.toString());
            notificationRepository.save(newNotification);
            log.debug("Save notification: " + newNotification);
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }

    class InstantDeserializer implements JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Instant.parse(json.getAsString());
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
