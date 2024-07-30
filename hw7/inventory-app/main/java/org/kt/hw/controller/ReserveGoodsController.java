package org.kt.hw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.GoodsReservations;
import org.kt.hw.model.ReserveGoodsRequest;
import org.kt.hw.repository.ReserveGoodsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/reserveGoods")
@RequiredArgsConstructor
@Slf4j
public class ReserveGoodsController {
    private final ReserveGoodsRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> reserveGoods(@RequestBody ReserveGoodsRequest request) {
        try {
            for (int goodId: request.getGoodIds()) {
                GoodsReservations reserveGood = ReserveGoodsRepository.reserveGood(request.getOrderId(), goodId);
                repository.save(reserveGood);
            }
            log.debug("goods reserved successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}