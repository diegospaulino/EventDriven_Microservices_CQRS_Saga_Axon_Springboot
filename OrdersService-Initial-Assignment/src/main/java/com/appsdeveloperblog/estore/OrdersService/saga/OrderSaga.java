package com.appsdeveloperblog.estore.OrdersService.saga;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.appsdeveloperblog.estore.OrdersService.core.events.OrderCreatedEvent;
import com.appsdeveloperblog.estore.core.User;
import com.appsdeveloperblog.estore.core.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.estore.core.commands.ReserveProductCommand;
import com.appsdeveloperblog.estore.core.events.ProductReservedEvent;
import com.appsdeveloperblog.estore.core.query.FetchUserPaymentDetailsQuery;

@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) { 

       ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .orderId(orderCreatedEvent.getOrderId())
                .userId(orderCreatedEvent.getUserId())
                .build();
        
        LOGGER.info("OrderCreatedEvent criado para orderId: " + reserveProductCommand.getOrderId() + 
                    " and productId: " + reserveProductCommand.getProductId());

        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage,
                    @Nonnull CommandResultMessage<? extends Object> commandResultMessage) {
                if(commandResultMessage.isExceptional()) {
                    //Inicia transação de compensação
                }
            }

        }); 
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        // Esse método pode ser usado para lidar com o evento de produto reservado
        // e atualizar o estado do saga, se necessário.
        // Por exemplo, você pode confirmar a reserva do produto ou iniciar outras ações.
        // Método para processar com o pagamento do usuário.

        LOGGER.info("ProductReservedEvent foi chamado para o produto com productId: " + productReservedEvent.getProductId() + 
                    " e orderId: " + productReservedEvent.getOrderId());

        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User userPaymentDetails = null;
        try {
            userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception e) {
            LOGGER.error("Erro ao buscar detalhes de pagamento do usuário: " + e.getMessage());
            
            // Start a compensating transaction here if needed
            return;
        }

        if(userPaymentDetails == null || userPaymentDetails.getPaymentDetails() == null) {
            LOGGER.error("Detalhes de pagamento do usuário não encontrados para o userId: " + productReservedEvent.getUserId());
            // Start a compensating transaction here if needed
            return;

        }

        LOGGER.info("O pagamento do usuário " + userPaymentDetails.getFirstName() + " foi encontrado com sucesso");

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .paymentId(UUID.randomUUID().toString())
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .build();

        String result = null;
        
        try {
            result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("Erro ao processar o pagamento: " + e.getMessage());

            // Start a compensating transaction here if needed
        }
        
        if(result == null) {
            LOGGER.info("O pagamento não foi processado para o orderId: " + productReservedEvent.getOrderId() + 
                        " e productId: " + productReservedEvent.getProductId());
            // Start a compensating transaction here if needed
        }
        
    }
}
