package com.thedevd.springboot.service;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// @FeignClient(name = "inventory-service", url = "localhost:8082")
// @FeignClient(name = "inventory-service")
// zuul: tell the Feign client to talk to Zuul API gateway instead of  end microservice.
@FeignClient(name = "netflix-zuul-api-gateway-server")
// @RibbonClient(name = "inventory-service")
public interface InventoryServiceFeignClient {

	// zuul: update all the mappings to start with application-name of inventory-microservice application.
	//@GetMapping("/api/inventory/{productCode}")
	@GetMapping("/inventory-service/api/inventory/{productCode}")
	public InventoryItemResponse getInventoryByProductCode(@PathVariable("productCode") String productCode);
}
