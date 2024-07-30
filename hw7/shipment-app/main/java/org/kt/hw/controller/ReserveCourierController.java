package org.kt.hw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.Courier;
import org.kt.hw.entity.CourierReservations;
import org.kt.hw.model.ReserveCourierRequest;
import org.kt.hw.repository.CourierRepository;
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
@RequestMapping("/reserveCourier")
@RequiredArgsConstructor
@Slf4j
public class ReserveCourierController {
    private final CourierRepository courierRepository;
    private final ReserveCourierRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> reserveCourier(@RequestBody ReserveCourierRequest request) {
        Courier freeCourier;
        try {
            freeCourier = courierRepository.findFreeCourier();
        } catch (Exception ignore) {
            log.debug("not found free courier");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not found free courier");
        }

        try {
            CourierReservations reserveCourier = ReserveCourierRepository.reserveCourier(request.getOrderId(), freeCourier.getId(), "");
            repository.save(reserveCourier);
            log.debug("courier reserved successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}