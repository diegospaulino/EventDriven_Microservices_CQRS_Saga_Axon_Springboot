package com.appsdeveloperblog.estore.PaymentsService.events;

import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.PaymentsService.data.PaymentEntity;
import com.appsdeveloperblog.estore.PaymentsService.data.PaymentsRepository;
import com.appsdeveloperblog.estore.core.events.PaymentProcessedEvent;

@Component
public class PaymentEventsHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentEventsHandler.class);
    private final PaymentsRepository paymentsRepository;

    public PaymentEventsHandler(PaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    @EventHandler
    public void on(PaymentProcessedEvent event) {
        LOGGER.info("Evento de processamento de pagamento foi chamado para o pedido com ID: " + event.getOrderId());

        // Create a new PaymentEntity and save it to the repository
        PaymentEntity paymentEntity = new PaymentEntity();
        BeanUtils.copyProperties(event, paymentEntity);

        paymentsRepository.save(paymentEntity);

    }

}
