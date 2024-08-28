package org.kt.hw.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.auth.Auth;
import org.kt.hw.entity.ExcursionReservations;
import org.kt.hw.entity.ExcursionSchedule;
import org.kt.hw.model.ReserveExcursionRequest;
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
@RequestMapping("/reserveExcursion")
@RequiredArgsConstructor
@Slf4j
public class ReserveExcursionController {
    private final ExcursionScheduleRepository scheduleRepository;
    private final ReserveExcursionRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> reserveExcursion(@RequestBody ReserveExcursionRequest data, HttpServletRequest request) {
        try {
            User user = Auth.preHandle(request);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            ExcursionSchedule slot = scheduleRepository.findExcursionScheduleByLocationIdAndExcursionIdAndStartSlot(data.getLocationId(), data.getExcursionId(), data.getStart());
            if (slot == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot not found");
            }
            if (slot.getСount() + data.getCount() > slot.getMaxCount()) {
                // Пытаемся зарезервировать билетов больше, чем есть в наличии - ошибка
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough tickets");
            }

            // Резервируем билеты
            ExcursionReservations reserveExcursion = ReserveExcursionRepository.reserveExcursion(data.getOrderId(), slot.getId(), data.getCount());
            repository.save(reserveExcursion);
            // Увеличивает кол-во купленных билетов
            slot.setСount(slot.getСount() + data.getCount());
            scheduleRepository.save(slot);
            log.debug("excursion reserved successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}