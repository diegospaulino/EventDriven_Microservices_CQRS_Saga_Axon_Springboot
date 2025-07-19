package com.passdeveloperblog.estore.ProductsService.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.passdeveloperblog.estore.ProductsService.core.events.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {

    @EventHandler
    public void on(ProductCreatedEvent event) {
        // This method is intentionally left empty.
        // The ProductLookupEntity will be created by the ProductLookupEntityListener
        // when the ProductCreatedEvent is published.
    }   
}
