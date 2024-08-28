package org.kt.hw.repository;

import org.kt.hw.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    void deleteAllByOrderId(int orderId);
    Payment findPaymentByOrderId(Integer orderId);

    // Создать оплату
    public static Payment createPayment(int orderId, int amount) {
        return new Payment(
                null,
                orderId,
                amount
        );
    }


}
