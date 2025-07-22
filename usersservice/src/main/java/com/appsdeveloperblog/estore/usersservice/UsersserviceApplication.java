package com.appsdeveloperblog.estore.usersservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import com.appsdeveloperblog.estore.core.config.XStreamConfig;

@EnableDiscoveryClient
@SpringBootApplication
@Import({ XStreamConfig.class }) // Importa a configuração do XStream
public class UsersserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersserviceApplication.class, args);
	}

}
