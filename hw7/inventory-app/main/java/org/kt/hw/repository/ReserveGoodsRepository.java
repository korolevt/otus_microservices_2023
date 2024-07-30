package org.kt.hw.repository;

import org.kt.hw.entity.GoodsReservations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserveGoodsRepository extends JpaRepository<GoodsReservations, Integer> {
    void deleteAllByOrderId(int orderId);

    // Создать оплату
    public static GoodsReservations reserveGood(int orderId, int goodId) {
        return new GoodsReservations(
                null,
                orderId,
                goodId
        );
    }


}
