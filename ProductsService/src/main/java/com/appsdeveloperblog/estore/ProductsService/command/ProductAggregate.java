package com.appsdeveloperblog.estore.ProductsService.command;

import java.math.BigDecimal;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.appsdeveloperblog.estore.ProductsService.core.events.ProductCreatedEvent;
import com.appsdeveloperblog.estore.core.commands.CancelProductReservationCommand;
import com.appsdeveloperblog.estore.core.commands.ReserveProductCommand;
import com.appsdeveloperblog.estore.core.events.ProductReservationCancelledEvent;
import com.appsdeveloperblog.estore.core.events.ProductReservedEvent;

@Aggregate(
        snapshotTriggerDefinition = "productSnapshotTriggerDefinition"
)
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;

    @SuppressWarnings("unused")
    private String title;

    @SuppressWarnings("unused")
    private BigDecimal price;
    
    private Integer quantity;

    public ProductAggregate() {

    }

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) {
        //Valida Create Product Command
        
        if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço não pode ser menor ou igual a zero!");

        }

        if(createProductCommand.getTitle() == null || createProductCommand.getTitle().isBlank()) {
            throw new IllegalArgumentException("Título não pode ser nulo ou vazio!");
        }

        //Copia os dados do objeto createProductCommand para o objeto productCreatedEvent
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

        //Dispara para os diferentes eventos desse aggregate um evento 
        //informado que o estado desse aggregate pode ser atualizado com novas informações
        AggregateLifecycle.apply(productCreatedEvent);
    }

    @CommandHandler
    public void handle(ReserveProductCommand reserveProductCommand) {

        if(quantity < reserveProductCommand.getQuantity()) {
            throw new IllegalArgumentException("Quantidade solicitada é maior que a quantidade disponível em estoque!");
        }

        ProductReservedEvent productReservedEvent = ProductReservedEvent.builder()
                .productId(reserveProductCommand.getProductId())
                .quantity(reserveProductCommand.getQuantity())
                .orderId(reserveProductCommand.getOrderId())
                .userId(reserveProductCommand.getUserId())
                .build();
        AggregateLifecycle.apply(productReservedEvent);
    }

    @CommandHandler
    public void handle(CancelProductReservationCommand cancelProductReservationCommand) {
        //Esse método é chamado quando o comando CancelProductReservationCommand é disparado
        //O Axon Framework irá automaticamente chamar esse método para atualizar o estado do aggregate
        //com as informações do comando recebido

        ProductReservationCancelledEvent productReservationCancelledEvent = ProductReservationCancelledEvent.builder()
                .productId(cancelProductReservationCommand.getProductId())
                .quantity(cancelProductReservationCommand.getQuantity())
                .orderId(cancelProductReservationCommand.getOrderId())
                .userId(cancelProductReservationCommand.getUserId())
                .reason(cancelProductReservationCommand.getReason())
                .build();

        AggregateLifecycle.apply(productReservationCancelledEvent);

    }

    @EventSourcingHandler
    public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
        //Esse método é chamado quando o evento ProductReservationCancelledEvent é disparado
        //O Axon Framework irá automaticamente chamar esse método para atualizar o estado do aggregate
        //com as informações do evento recebido

        this.quantity += productReservationCancelledEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        //Esse método é chamado quando o evento ProductCreatedEvent é disparado
        //O Axon Framework irá automaticamente chamar esse método para atualizar o estado do aggregate
        //com as informações do evento recebido

        this.productId = productCreatedEvent.getProductId();
        this.title = productCreatedEvent.getTitle();
        this.price = productCreatedEvent.getPrice();
        this.quantity = productCreatedEvent.getQuantity();
    }   

    @EventSourcingHandler
    public void on(ProductReservedEvent productReservedEvent) {
        //Esse método é chamado quando o evento ProductReservedEvent é disparado
        //O Axon Framework irá automaticamente chamar esse método para atualizar o estado do aggregate
        //com as informações do evento recebido

        this.quantity -= productReservedEvent.getQuantity();
    }
}
