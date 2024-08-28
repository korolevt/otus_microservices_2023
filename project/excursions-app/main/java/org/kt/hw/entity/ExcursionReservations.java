package org.kt.hw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="excursion_reservation")
public class ExcursionReservations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name="order_id")
    Integer orderId;

    @Column(name = "excursion_schedule_id")
    Integer scheduleId;

    // Кол-во заказанных билетов
    @Column(name = "count")
    Integer count;
}
