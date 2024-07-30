package org.kt.hw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.model.CancelRequest;
import org.kt.hw.repository.ReserveCourierRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/cancelCourierReservation")
@RequiredArgsConstructor
@Slf4j
public class CancelCourierReservationController {
    private final ReserveCourierRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> cancelCourierReservation(@RequestBody CancelRequest cancelRequest) {
        try {
            repository.deleteAllByOrderId(cancelRequest.getOrderId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
