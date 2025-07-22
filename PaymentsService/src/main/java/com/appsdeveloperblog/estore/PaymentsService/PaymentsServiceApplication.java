package com.appsdeveloperblog.estore.PaymentsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.appsdeveloperblog.estore.core.config.XStreamConfig;

@EnableDiscoveryClient
@SpringBootApplication
@Import({ XStreamConfig.class }) // Importa a configuração do XStream
public class PaymentsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentsServiceApplication.class, args);
	}

}
