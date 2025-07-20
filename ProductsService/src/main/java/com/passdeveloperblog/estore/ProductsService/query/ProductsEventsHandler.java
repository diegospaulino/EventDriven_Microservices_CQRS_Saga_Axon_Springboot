package com.passdeveloperblog.estore.ProductsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.passdeveloperblog.estore.ProductsService.core.data.ProductEntity;
import com.passdeveloperblog.estore.ProductsService.core.data.ProductsRepository;
import com.passdeveloperblog.estore.ProductsService.core.events.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductsEventsHandler {

    private final ProductsRepository productsRepository;

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
        System.out.println("====================================================");
        System.out.println("Product created: " + event.getProductId() + ", Title: " + event.getTitle());
        System.out.println("====================================================");
        
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);

        try {
            productsRepository.save(productEntity);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if(true){
            throw new Exception("Forçando a chamada de exceção na classe de Events Handler");
        }
        
    }
}
