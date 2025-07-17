package com.passdeveloperblog.estore.ProductsService.rest;

import java.util.UUID;

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
public class ProductsController {

    //Variável para acessar, não somente as variáveis de ambiente do projeto, como também suas propriedades de configuração
    @Autowired
    private Environment environment;

    @PostMapping
    public String createProduct(@RequestBody CreateProductsRestModel createProductsRestModel){

        CreateProductCommand createProductCommand = CreateProductCommand.builder()
            .price(createProductsRestModel.getPrice())
            .quantity(createProductsRestModel.getQuantity())
            .title(createProductsRestModel.getTitle())
            .productId(UUID.randomUUID().toString())
            .build();


        return "HTTP POST Handled - Title is " + createProductsRestModel.getTitle();
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
