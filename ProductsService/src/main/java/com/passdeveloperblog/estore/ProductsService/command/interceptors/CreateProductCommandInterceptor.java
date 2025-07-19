package com.passdeveloperblog.estore.ProductsService.command.interceptors;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.passdeveloperblog.estore.ProductsService.command.CreateProductCommand;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    @Override
    @Nonnull
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> arg0) {
        
        return (index, command) -> {
            LOGGER.info("Interceptando o comando: " + command.getPayloadType());

            if(CreateProductCommand.class.equals(command.getPayloadType())){
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                
                if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Preço não pode ser menor ou igual a zero!");

                 }

                if(createProductCommand.getTitle() == null || createProductCommand.getTitle().isBlank()) {
                    throw new IllegalArgumentException("Título não pode ser nulo ou vazio!");
                }
            }

            return command;
        };
    }

}
