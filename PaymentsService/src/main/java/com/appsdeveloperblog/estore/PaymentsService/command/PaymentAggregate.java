package com.appsdeveloperblog.estore.PaymentsService.command;

import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.appsdeveloperblog.estore.core.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.estore.core.events.PaymentProcessedEvent;

@Aggregate
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;

    @SuppressWarnings("unused")
    private String orderId;

    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
        // Valida Process Payment Command
        if(processPaymentCommand.getPaymentDetails() == null) {
            throw new IllegalArgumentException("Pedido com detalhes em branco!");
        }
        if (processPaymentCommand.getOrderId() == null || processPaymentCommand.getOrderId().isBlank()) {
            throw new IllegalArgumentException("Id do pedido não pode ser nulo ou vazio!");
        }

        if (processPaymentCommand.getPaymentId() == null || processPaymentCommand.getPaymentId().isBlank()) {
            throw new IllegalArgumentException("Id do pagamento não pode ser nulo ou vazio!");
        }

        PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(processPaymentCommand.getPaymentId(), processPaymentCommand.getOrderId());
        AggregateLifecycle.apply(paymentProcessedEvent);

    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        // Esse método é chamado quando o evento PaymentProcessedEvent é disparado
        // O Axon Framework irá automaticamente chamar esse método para atualizar o estado do aggregate
        // com as informações do evento recebido

        this.paymentId = paymentProcessedEvent.getPaymentId();
        this.orderId = paymentProcessedEvent.getOrderId();

    }
}
