package com.passdeveloperblog.estore.ProductsService.command.rest;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreateProductsRestModel {

    private String title;
    private BigDecimal price;
    private Integer quantity;
}
