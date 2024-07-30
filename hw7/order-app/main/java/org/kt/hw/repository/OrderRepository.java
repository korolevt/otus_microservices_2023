package org.kt.hw.repository;

import org.kt.hw.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
//    Account findAccountByOwnerId(Integer ownerId);

    // Создать новый заказ
    public static Order createOrder() {
        return new Order(
                null
        );
    }


}
