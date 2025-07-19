package com.passdeveloperblog.estore.ProductsService.query;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.passdeveloperblog.estore.ProductsService.core.data.ProductEntity;
import com.passdeveloperblog.estore.ProductsService.core.data.ProductsRepository;
import com.passdeveloperblog.estore.ProductsService.query.rest.ProductRestModel;

@Component
public class ProductsQueryHandler {

    private final ProductsRepository productsRepository;

    public ProductsQueryHandler(ProductsRepository productsRepository) {
        // Constructor logic if needed
        this.productsRepository = productsRepository;
    }

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductsQuery query) {
        // Logic to find products using productsRepository
        
        List<ProductRestModel> productsRest = new ArrayList<>();

        List<ProductEntity> storedProducts = productsRepository.findAll();
        for(ProductEntity productEntity : storedProducts) {
            
            ProductRestModel productRestModel = new ProductRestModel();
            BeanUtils.copyProperties(productEntity, productRestModel);
            productsRest.add(productRestModel);
        }
        
        return productsRest;
    } 
}
