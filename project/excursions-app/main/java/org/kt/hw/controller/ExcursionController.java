package org.kt.hw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.ExcursionReservations;
import org.kt.hw.entity.ExcursionSchedule;
import org.kt.hw.model.ExcursionRequest;
import org.kt.hw.model.GetReserveExcursionsResponse;
import org.kt.hw.model.GetPriceResponse;
import org.kt.hw.repository.ExcursionScheduleRepository;
import org.kt.hw.repository.ReserveExcursionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/excursions")
@RequiredArgsConstructor
@Slf4j
public class ExcursionController {
    private final ExcursionScheduleRepository scheduleRepository;
    private final ReserveExcursionRepository repository;

    @GetMapping("test")
    void test() {
        List<ExcursionSchedule> list = scheduleRepository.findAll();
    }

    @PostMapping("reserved")
    public ResponseEntity<GetReserveExcursionsResponse> getReserveExcursions(@RequestBody ExcursionRequest request) {
        ExcursionSchedule slot = scheduleRepository.findExcursionScheduleByLocationIdAndExcursionIdAndStartSlot(request.getLocationId(), request.getExcursionId(), request.getStart());
        if (slot == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot not found");
        }
        List<ExcursionReservations> reservations = repository.findAllByScheduleId(slot.getId());
        List<Integer> orders = reservations.stream().map(reserve -> reserve.getOrderId()).collect(Collectors.toList());
        return ResponseEntity.ok(new GetReserveExcursionsResponse(orders));
    }

    @PostMapping("price")
    public ResponseEntity<GetPriceResponse> getPrice(@RequestBody ExcursionRequest request) {
        ExcursionSchedule slot = scheduleRepository.findExcursionScheduleByLocationIdAndExcursionIdAndStartSlot(request.getLocationId(), request.getExcursionId(), request.getStart());
        if (slot == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot not found");
        }
        return ResponseEntity.ok(new GetPriceResponse(slot.getPrice()));
    }
}
