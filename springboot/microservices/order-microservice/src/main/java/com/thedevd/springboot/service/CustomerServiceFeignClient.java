package com.thedevd.springboot.service;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "customer-service", url = "localhost:8062")
// @FeignClient(name = "customer-service")
//zuul: tell the Feign client to talk to Zuul API gateway instead of  end microservice.
@FeignClient(name = "netflix-zuul-api-gateway-server")
// @RibbonClient(name = "customer-service")
public interface CustomerServiceFeignClient {

	// zuul: update all the mappings to start with application-name of inventory-microservice application.
	//@GetMapping("/api/customer/phone/{phoneNo}")
	@GetMapping("/customer-service/api/customer/phone/{phoneNo}")
	public CustomerDetailResponse getCustomerDetailsByPhoneNo(@PathVariable("phoneNo") String phoneNo);
}
