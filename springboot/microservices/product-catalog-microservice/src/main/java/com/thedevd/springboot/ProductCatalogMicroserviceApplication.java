package com.thedevd.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ProductCatalogMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductCatalogMicroserviceApplication.class, args);
	}

}
