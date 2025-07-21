package com.appsdeveloperblog.estore.OrdersService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.appsdeveloperblog.estore.core.config.XStreamConfig;

@EnableDiscoveryClient
@SpringBootApplication
@Import({ XStreamConfig.class }) // Importa a configuração do XStream
public class OrdersServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersServiceApplication.class, args);
	}

}
