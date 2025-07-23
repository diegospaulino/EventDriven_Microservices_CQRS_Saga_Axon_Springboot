package com.appsdeveloperblog.estore.ProductsService.command;

import java.math.BigDecimal;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateProductCommand {

    //Variável que o Axon Framework utilizará como identificador para associá-lo a um objeto agregado quando for acionado.
    @TargetAggregateIdentifier
    private final String productId;

    private final String title;
    private final BigDecimal price;
    private final Integer quantity;
}
