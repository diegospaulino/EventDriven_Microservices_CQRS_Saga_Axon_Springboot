package com.passdeveloperblog.estore.ProductsService.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
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

    @EventHandler
    public void on(ProductCreatedEvent event) {
        // Handle the product creation event
        // This method can be used to update read models or perform other actions
        System.out.println("====================================================");
        System.out.println("Product created: " + event.getProductId() + ", Title: " + event.getTitle());
        System.out.println("====================================================");
        
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);

        productsRepository.save(productEntity);
    }
}
