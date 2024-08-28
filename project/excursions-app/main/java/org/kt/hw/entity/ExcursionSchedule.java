package org.kt.hw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="excursion_schedule")
public class ExcursionSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name="location_id")
    Integer locationId;

    @Column(name="excursion_id")
    Integer excursionId;

    @Column(name="start_slot")
    Instant startSlot;

    @Column(name="price")
    Integer price;

    // максимальное кол-во билетов
    @Column(name="max_count")
    Integer maxCount;

    // заказано билетов
    @Column(name="count")
    Integer сount;
}
