package com.thedevd.springboot.service;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// @FeignClient(name = "inventory-service", url = "localhost:8082")
@FeignClient(name = "inventory-service")
@RibbonClient(name = "inventory-service")
public interface InventoryServiceFeignClient {

	@GetMapping("/api/inventory/{productCode}")
	public InventoryItemResponse getInventoryByProductCode(@PathVariable("productCode") String productCode);
}
