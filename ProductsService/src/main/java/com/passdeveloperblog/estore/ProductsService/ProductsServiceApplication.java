package com.passdeveloperblog.estore.ProductsService;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

import com.passdeveloperblog.estore.ProductsService.command.interceptors.CreateProductCommandInterceptor;
import com.passdeveloperblog.estore.ProductsService.core.errorhandling.ProductsServiceEventsErrorHandling;

@EnableDiscoveryClient
@SpringBootApplication
public class ProductsServiceApplication {

	//Caso ocorram erros no Axon Server relativos a XStream, verificar a correção feita na aula 58 - Fixing ForbiddenClassException
	public static void main(String[] args) {
		SpringApplication.run(ProductsServiceApplication.class, args);
	}

	@Autowired
	public void registerCreateProductCommandInterceptor(ApplicationContext context, CommandBus commandBus) {
		// Método para registrar o interceptor de comando CreateProductCommand
		// Este método pode ser chamado no contexto de inicialização do Spring
		// ou em outro local apropriado para garantir que o interceptor seja registrado.

		commandBus.registerDispatchInterceptor(context.getBean(CreateProductCommandInterceptor.class));
	}

	@Autowired
	public void configure(EventProcessingConfigurer configurer) {
		// Método para configurar o processamento de eventos
		// Este método pode ser usado para registrar manipuladores de eventos, interceptadores, etc.
		configurer.registerListenerInvocationErrorHandler("product-group", 
				configure -> new ProductsServiceEventsErrorHandling());

		// //Caso quisesse utilizar a classe pré-programada já vinda com o axon Framework, ao invés de criar um EventsErrorHandling personalizado
		// configurer.registerListenerInvocationErrorHandler("product-group", 
		// 		configure -> PropagatingErrorHandler.instance());
	}

}
