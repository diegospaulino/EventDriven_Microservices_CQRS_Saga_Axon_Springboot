package com.appsdeveloperblog.estore.ProductsService;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.appsdeveloperblog.estore.ProductsService.command.interceptors.CreateProductCommandInterceptor;
import com.appsdeveloperblog.estore.ProductsService.core.errorhandling.ProductsServiceEventsErrorHandling;
import com.appsdeveloperblog.estore.core.config.XStreamConfig;

@EnableDiscoveryClient
@SpringBootApplication
@Import({ XStreamConfig.class }) // Importa a configuração do XStream
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

	@Bean(name = "productSnapshotTriggerDefinition")
	public SnapshotTriggerDefinition productSnapshotTriggerDefinition(Snapshotter snapshotter) {
		// Método para definir o gatilho de snapshot
		// Este método pode ser usado para configurar o comportamento do snapshot no Axon Framework
		return new EventCountSnapshotTriggerDefinition(snapshotter, 3);
	}
}
