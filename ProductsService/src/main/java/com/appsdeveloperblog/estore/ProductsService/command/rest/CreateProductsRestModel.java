package com.appsdeveloperblog.estore.ProductsService.command.rest;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProductsRestModel {

    @NotBlank(message = "Título do produto é um campo obrigatório!")
    private String title;
    
    @Min(value = 1, message = "Preço do produto não pode ser menor que 1!")
    private BigDecimal price;
    
    @Min(value = 1, message = "Quantidade do produto não pode ser menor que 1!")
    @Max(value = 5, message = "Quantidade do produto não pode ser maior que 5!")
    private Integer quantity;
}
