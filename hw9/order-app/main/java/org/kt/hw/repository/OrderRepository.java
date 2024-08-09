package org.kt.hw.repository;

import org.kt.hw.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
//    Account findAccountByOwnerId(Integer ownerId);
    Order findOrderByIdempotencyKeyEquals(String idempotencyKey);

    // Создать новый заказ
    public static Order createOrder(String idempotencyKey) {
        return new Order(
                null,
                idempotencyKey
        );
    }


}
