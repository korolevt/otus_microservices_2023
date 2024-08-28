package org.kt.hw.repository;

import org.kt.hw.entity.ExcursionReservations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReserveExcursionRepository extends JpaRepository<ExcursionReservations, Integer> {
    void deleteAllByOrderId(int orderId);

    ExcursionReservations findExcursionReservationsByOrderId(int orderId);

    List<ExcursionReservations> findAllByScheduleId(int scheduleId);

    // Бронировать экскурсию
    public static ExcursionReservations reserveExcursion(int orderId, int scheduleId, int count) {
        return new ExcursionReservations(
                null,
                orderId,
                scheduleId,
                count
        );
    }


}
