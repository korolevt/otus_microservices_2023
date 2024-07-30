package org.kt.hw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="courier_reservations")
public class CourierReservations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name="order_id")
    Integer orderId;

    @Column(name = "courier_id")
    Integer courierId;

    @Column(name = "destination")
    String destination;
}
