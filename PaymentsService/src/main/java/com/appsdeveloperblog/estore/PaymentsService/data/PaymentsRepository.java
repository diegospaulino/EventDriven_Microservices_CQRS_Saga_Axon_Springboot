package com.appsdeveloperblog.estore.PaymentsService.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<PaymentEntity, String> {
    
    // Additional query methods can be defined here if needed
    // For example, to find payments by order ID:
    // List<PaymentEntity> findByOrderId(String orderId);

}
