package org.kt.hw.repository;

import org.kt.hw.entity.CourierReservations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserveCourierRepository extends JpaRepository<CourierReservations, Integer> {
    void deleteAllByOrderId(int orderId);

    // Зарезервировать курьера
    public static CourierReservations reserveCourier(int orderId, int courierId, String distination) {
        return new CourierReservations(
                null,
                orderId,
                courierId,
                distination
        );
    }


}
