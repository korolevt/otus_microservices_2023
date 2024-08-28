package org.kt.hw.repository;

import org.kt.hw.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
//    Account findAccountByOwnerId(Integer ownerId);
    List<Notification> findNotificationsByUserId(Integer userId);

    // Создать нотификацию
    public static Notification createNotification(Integer userId, String message) {
        return new Notification(
                null,
                userId,
                message
        );
    }


}
