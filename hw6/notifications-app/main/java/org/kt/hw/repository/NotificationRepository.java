package org.kt.hw.repository;

import org.kt.hw.entity.Notification;
import org.kt.hw.model.NotificationDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
//    Account findAccountByOwnerId(Integer ownerId);
    List<Notification> findNotificationsByUserId(Integer userId);

    // Создать нотификацию
    public static Notification createNotification(Integer userId, String text) {
        return new Notification(
                null,
                userId,
                text
        );
    }


}
