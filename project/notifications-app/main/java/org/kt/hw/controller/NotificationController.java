package org.kt.hw.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.auth.Auth;
import org.kt.hw.entity.Notification;
import org.kt.hw.model.User;
import org.kt.hw.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationRepository notificationRepository;


    @RequestMapping(value = "/notifications", method = RequestMethod.GET)
    public ResponseEntity<List<Notification>> getNotifications(HttpServletRequest request) {
        User user = Auth.preHandle(request);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.status(HttpStatus.OK).body(notificationRepository.findNotificationsByUserId(user.getId()));
    }

    @RequestMapping(value = "/notifications", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAll() {
        notificationRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

}
