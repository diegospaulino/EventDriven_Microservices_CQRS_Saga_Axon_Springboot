package com.appsdeveloperblog.estore.ProductsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.ProductsService.core.data.ProductEntity;
import com.appsdeveloperblog.estore.ProductsService.core.data.ProductsRepository;
import com.appsdeveloperblog.estore.ProductsService.core.events.ProductCreatedEvent;
import com.appsdeveloperblog.estore.core.events.ProductReservationCancelledEvent;
import com.appsdeveloperblog.estore.core.events.ProductReservedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductsEventsHandler {

    private final ProductsRepository productsRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsEventsHandler.class);

    public ProductsEventsHandler(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handleGeneralException(Exception e) throws Exception {
        // This method can be used to handle specific exceptions if needed
        throw e;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handleIllegalArgumentException(IllegalArgumentException e) {
        // This method can be used to handle specific exceptions if needed
        //Log error message
    }

    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception{
        // Handle the product creation event
        // This method can be used to update read models or perform other actions
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);

        try {
            productsRepository.save(productEntity);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        // if(true){
        //     throw new Exception("Forçando a chamada de exceção na classe de Events Handler");
        // }
        
    }

    @EventHandler
    public void on(ProductReservedEvent productReservedEvent) {

        ProductEntity productEntity = productsRepository.findByProductId(productReservedEvent.getProductId());
        productEntity.setQuantity(productEntity.getQuantity() - productReservedEvent.getQuantity());

        productsRepository.save(productEntity);
        LOGGER.info("ProductReservedEvent foi chamado para o produto com productId: " + productReservedEvent.getProductId() + 
                    " e orderId: " + productReservedEvent.getOrderId());
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
        // Esse método é chamado quando o evento ProductReservationCancelledEvent é disparado
        // O Axon Framework irá automaticamente chamar esse método para atualizar o estado do aggregate
        // com as informações do evento recebido

        ProductEntity currentlyStoredProduct = productsRepository.findByProductId(productReservationCancelledEvent.getProductId());
        int newQuantity = currentlyStoredProduct.getQuantity() + productReservationCancelledEvent.getQuantity();
        currentlyStoredProduct.setQuantity(newQuantity);

        productsRepository.save(currentlyStoredProduct);
        LOGGER.info("ProductReservationCancelledEvent foi chamado para o produto com productId: " + productReservationCancelledEvent.getProductId() + 
                    " e orderId: " + productReservationCancelledEvent.getOrderId());
        
    }
}
