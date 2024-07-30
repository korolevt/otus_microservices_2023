package org.kt.hw.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.kt.hw.entity.Courier;
import org.kt.hw.entity.CourierReservations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CourierRepository {

    private final EntityManager em;

    // Найти свободного курьера
    public Courier findFreeCourier() {
        Query q = em.createNativeQuery(
                "select * FROM couriers \n" +
                        "WHERE id NOT IN (SELECT courier_id FROM courier_reservations) LIMIT 1"
                , Courier.class);

        return (Courier) q.getSingleResult();
    }

}
