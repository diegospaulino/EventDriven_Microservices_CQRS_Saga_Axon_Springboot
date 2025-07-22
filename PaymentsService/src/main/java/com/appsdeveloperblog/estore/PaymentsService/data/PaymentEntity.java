package com.appsdeveloperblog.estore.PaymentsService.data;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "payments")
public class PaymentEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    private String paymentId;

    @Column
    private String orderId;

    // Additional fields and methods can be added as needed

}
