package com.passdeveloperblog.estore.ProductsService.command.rest;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passdeveloperblog.estore.ProductsService.command.CreateProductCommand;

@RestController
@RequestMapping("/products") //http://localhost:8080/products
public class ProductsCommandController {

    //Variável para acessar, não somente as variáveis de ambiente do projeto, como também suas propriedades de configuração
    /*@Autowired
    *private Environment environment;
    */

    private final Environment environment;
    private final CommandGateway commandGateway;

    @Autowired
    //Construtor para injeção de dependência do Spring
    public ProductsCommandController(Environment environment, CommandGateway commandGateway) {
        this.environment = environment;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@RequestBody CreateProductsRestModel createProductsRestModel){

        CreateProductCommand createProductCommand = CreateProductCommand.builder()
            .price(createProductsRestModel.getPrice())
            .quantity(createProductsRestModel.getQuantity())
            .title(createProductsRestModel.getTitle())
            .productId(UUID.randomUUID().toString())
            .build();

        String returnedValue;
        
        try {
            returnedValue = commandGateway.sendAndWait(createProductCommand).toString();
        } catch (Exception e) {
            returnedValue = e.getLocalizedMessage();
        }
        
        return returnedValue;
    }

    @GetMapping
    public String getProduct(){
        //environment.getProperty("local.server.port") retorna a porta gerada randomicamente em que o servidor no ar está rodando
        return "HTTP GET Handled - Porta " + environment.getProperty("local.server.port");
    }

    @PutMapping
    public String updateProduct(){
        return "HTTP PUT Handled";
    }

    @DeleteMapping
    public String deleteProduct(){
        return "HTTP DELETE Handled";
    }
}
