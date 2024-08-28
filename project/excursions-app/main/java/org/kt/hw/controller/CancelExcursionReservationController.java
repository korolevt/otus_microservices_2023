package org.kt.hw.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.auth.Auth;
import org.kt.hw.entity.ExcursionReservations;
import org.kt.hw.entity.ExcursionSchedule;
import org.kt.hw.model.CancelRequest;
import org.kt.hw.model.CancelResponse;
import org.kt.hw.model.User;
import org.kt.hw.repository.ExcursionScheduleRepository;
import org.kt.hw.repository.ReserveExcursionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/cancelExcursionReservation")
@RequiredArgsConstructor
@Slf4j
public class CancelExcursionReservationController {
    private final ReserveExcursionRepository repository;
    private final ExcursionScheduleRepository scheduleRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<CancelResponse> cancelExcursionReservation(@RequestBody CancelRequest data, HttpServletRequest request) {
        try {
            User user = Auth.preHandle(request);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            ExcursionReservations excursionReservations = repository.findExcursionReservationsByOrderId(data.getOrderId());
            if (excursionReservations == null) {
                // Бронь не найдена
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation not found");
            }
            ExcursionSchedule slot = scheduleRepository.findExcursionScheduleById(excursionReservations.getScheduleId());
            if (slot == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot not found");
            }
            // Возвращаем билеты (уменьшаем кол-во купленных)
            slot.setСount(slot.getСount() - excursionReservations.getCount());
            scheduleRepository.save(slot);
            // Удаляем резерв
            repository.deleteAllByOrderId(data.getOrderId());
            return ResponseEntity.ok().body(new CancelResponse(data.getOrderId(), excursionReservations.getCount()));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
