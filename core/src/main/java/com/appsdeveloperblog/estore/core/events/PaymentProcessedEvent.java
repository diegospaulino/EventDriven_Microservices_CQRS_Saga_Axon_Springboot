package com.appsdeveloperblog.estore.core.events;

import lombok.Value;

@Value
public class PaymentProcessedEvent {

    private final String paymentId;
    private final String orderId;
}
