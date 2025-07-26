package com.appsdeveloperblog.estore.OrdersService.saga;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.appsdeveloperblog.estore.OrdersService.command.commands.ApproveOrderCommand;
import com.appsdeveloperblog.estore.OrdersService.command.commands.RejectOrderCommand;
import com.appsdeveloperblog.estore.OrdersService.core.events.OrderApprovedEvent;
import com.appsdeveloperblog.estore.OrdersService.core.events.OrderCreatedEvent;
import com.appsdeveloperblog.estore.OrdersService.core.events.OrderRejectedEvent;
import com.appsdeveloperblog.estore.OrdersService.core.model.OrderSummary;
import com.appsdeveloperblog.estore.OrdersService.query.FindOrderQuery;
import com.appsdeveloperblog.estore.core.User;
import com.appsdeveloperblog.estore.core.commands.CancelProductReservationCommand;
import com.appsdeveloperblog.estore.core.commands.ProcessPaymentCommand;
import com.appsdeveloperblog.estore.core.commands.ReserveProductCommand;
import com.appsdeveloperblog.estore.core.events.PaymentProcessedEvent;
import com.appsdeveloperblog.estore.core.events.ProductReservationCancelledEvent;
import com.appsdeveloperblog.estore.core.events.ProductReservedEvent;
import com.appsdeveloperblog.estore.core.query.FetchUserPaymentDetailsQuery;

@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE = "payment-processing-deadline";

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    private String scheduleId;

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
                    RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(orderCreatedEvent.getOrderId(), 
                                "Reserva de produto cancelada: " + commandResultMessage.exceptionResult().getMessage());

                    commandGateway.send(rejectOrderCommand);
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
            cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }

        if(userPaymentDetails == null || userPaymentDetails.getPaymentDetails() == null) {
            LOGGER.error("Detalhes de pagamento do usuário não encontrados para o userId: " + productReservedEvent.getUserId());
            // Start a compensating transaction here if needed
            cancelProductReservation(productReservedEvent, "Não foi possível encontrar os detalhes do pagamento do usuário!");
            return;

        }

        LOGGER.info("O pagamento do usuário " + userPaymentDetails.getFirstName() + " foi encontrado com sucesso");

        scheduleId = deadlineManager.schedule(Duration.of(120, ChronoUnit.SECONDS), PAYMENT_PROCESSING_TIMEOUT_DEADLINE, productReservedEvent);

        //Somente para garantir que o deadlineManager será lançado
        //remover após reste
        // if(true) {
        //     LOGGER.info("DeadlineManager foi iniciado para o orderId: " + productReservedEvent.getOrderId() + 
        //                 " e productId: " + productReservedEvent.getProductId());
        //     return;  
        // }

        ProcessPaymentCommand proccessPaymentCommand = ProcessPaymentCommand.builder()
        		.orderId(productReservedEvent.getOrderId())
        		.paymentDetails(userPaymentDetails.getPaymentDetails())
        		.paymentId(UUID.randomUUID().toString())
        		.build();

        String result = null;
        
        try {
            result = commandGateway.sendAndWait(proccessPaymentCommand);
        } catch (Exception e) {
            LOGGER.error("Erro ao processar o pagamento: " + e.getMessage());

            // Start a compensating transaction here if needed
            cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }
        
        if(result == null) {
            LOGGER.info("O pagamento não foi processado para o orderId: " + productReservedEvent.getOrderId() + 
                        " e productId: " + productReservedEvent.getProductId());
            // Start a compensating transaction here if needed
            cancelProductReservation(productReservedEvent, "Não foi possível processar o pagamento com as informações fornecidos!");
        }
        
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {

        cancelDeadline();

        CancelProductReservationCommand publishProductReservationCommand = CancelProductReservationCommand.builder()
                .productId(productReservedEvent.getProductId())
                .quantity(productReservedEvent.getQuantity())
                .orderId(productReservedEvent.getOrderId())
                .userId(productReservedEvent.getUserId())
                .reason(reason)
                .build();

        commandGateway.send(publishProductReservationCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        // Esse método pode ser usado para lidar com o evento de pagamento processado
        // e atualizar o estado do saga, se necessário.
        // Por exemplo, você pode confirmar o pagamento ou iniciar outras ações.

        LOGGER.info("PaymentProcessedEvent foi chamado para o orderId: " + paymentProcessedEvent.getOrderId() + 
                    " e paymentId: " + paymentProcessedEvent.getPaymentId());

        // Aqui você pode adicionar lógica adicional para lidar com o pagamento processado,
        // como atualizar o status do pedido ou notificar outros serviços.
        
        cancelDeadline();

        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.send(approveOrderCommand);

        // Finaliza o saga
        //SagaLifecycle.end();

    }

    private void cancelDeadline() {
        if(scheduleId != null) {
            LOGGER.info("Cancelando o deadline com ID: " + scheduleId);
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
            scheduleId = null;
        }
        
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        // Esse método pode ser usado para lidar com o evento de pedido aprovado
        // e atualizar o estado do saga, se necessário.
        // Por exemplo, você pode confirmar a aprovação do pedido ou iniciar outras ações.

        LOGGER.info("OrderApprovedEvent foi chamado para o pedido com ID: " + orderApprovedEvent.getOrderId());

        // Aqui você pode adicionar lógica adicional para lidar com o pedido aprovado,
        // como notificar outros serviços ou atualizar o status do pedido.
        
        // Finaliza o saga
        //SagaLifecycle.end();

        queryUpdateEmitter.emit(FindOrderQuery.class, query -> true, new OrderSummary(
                    orderApprovedEvent.getOrderId(), "", orderApprovedEvent.getOrderStatus()));

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        // Esse método pode ser usado para lidar com o evento de cancelamento de reserva de produto
        // e atualizar o estado do saga, se necessário.
        // Por exemplo, você pode confirmar o cancelamento da reserva ou iniciar outras ações.

        LOGGER.info("ProductReservationCancelledEvent foi chamado para o produto com productId: " + productReservationCancelledEvent.getProductId() + 
                    " e orderId: " + productReservationCancelledEvent.getOrderId());

        // Aqui você pode adicionar lógica adicional para lidar com o cancelamento da reserva,
        // como notificar outros serviços ou atualizar o status do pedido.
        
        // Cria e envia um RejectOrderCommand para rejeitar o pedido
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCancelledEvent.getOrderId(), 
                "Reserva de produto cancelada: " + productReservationCancelledEvent.getReason());

        commandGateway.send(rejectOrderCommand);
        // Finaliza o saga
        //SagaLifecycle.end();
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        // Esse método pode ser usado para lidar com o evento de pedido rejeitado
        // e atualizar o estado do saga, se necessário.
        // Por exemplo, você pode confirmar a rejeição do pedido ou iniciar outras ações.

        LOGGER.info("OrderRejectedEvent foi chamado com sucesso para o pedido com ID: " + orderRejectedEvent.getOrderId());

        queryUpdateEmitter.emit(FindOrderQuery.class, query -> true, 
                new OrderSummary(orderRejectedEvent.getOrderId(), orderRejectedEvent.getReason(), orderRejectedEvent.getOrderStatus()));
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
        // Esse método é chamado quando o deadline de processamento de pagamento expira
        // e pode ser usado para lidar com o timeout do pagamento.

        LOGGER.info("O deadline de processamento de pagamento expirou para o orderId: " + productReservedEvent.getOrderId() + 
                    " e productId: " + productReservedEvent.getProductId());

        // Aqui você pode adicionar lógica adicional para lidar com o timeout do pagamento,
        // como cancelar a reserva do produto ou notificar outros serviços.
        
        cancelProductReservation(productReservedEvent, "O prazo para processar o pagamento expirou!");

    }

}
