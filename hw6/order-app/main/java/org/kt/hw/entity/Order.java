package org.kt.hw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="orders")
public class Order {
    public enum State{
        StateNew,
        StatePaid,
        StateUnpaid
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name="creator_id")
    Integer creatorId;

    @Column(name = "title")
    String title;

    @Column(name = "price")
    Integer price;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    State state;
}
