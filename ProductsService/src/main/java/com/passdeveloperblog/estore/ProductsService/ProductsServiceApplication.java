package com.passdeveloperblog.estore.ProductsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ProductsServiceApplication {

	//Caso ocorram erros no Axon Server relativos a XStream, verificar a correção feita na aula 58 - Fixing ForbiddenClassException
	public static void main(String[] args) {
		SpringApplication.run(ProductsServiceApplication.class, args);
	}

}
