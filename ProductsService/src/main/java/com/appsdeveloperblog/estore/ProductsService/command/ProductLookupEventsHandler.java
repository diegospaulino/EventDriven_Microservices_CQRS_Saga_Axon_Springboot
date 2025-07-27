package com.appsdeveloperblog.estore.ProductsService.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.appsdeveloperblog.estore.ProductsService.core.data.ProductLookupEntity;
import com.appsdeveloperblog.estore.ProductsService.core.data.ProductLookupRepository;
import com.appsdeveloperblog.estore.ProductsService.core.events.ProductCreatedEvent;
import com.appsdeveloperblog.estore.ProductsService.query.ProductsEventsHandler;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {

    private final ProductLookupRepository productLookupRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsEventsHandler.class);

    public ProductLookupEventsHandler(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        ProductLookupEntity productLookupEntity = new ProductLookupEntity(event.getProductId(), event.getTitle());

        productLookupRepository.save(productLookupEntity);
    }   

    @ResetHandler
    public void reset() {
        productLookupRepository.deleteAll();
        LOGGER.info("Products repository has been reset.");
    }
}
