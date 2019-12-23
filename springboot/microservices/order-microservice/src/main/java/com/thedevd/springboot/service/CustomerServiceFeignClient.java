package com.thedevd.springboot.service;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "customer-service", url = "localhost:8062")
@FeignClient(name = "customer-service")
@RibbonClient(name = "customer-service")
public interface CustomerServiceFeignClient {

	@GetMapping("/api/customer/phone/{phoneNo}")
	public CustomerDetailResponse getCustomerDetailsByPhoneNo(@PathVariable("phoneNo") String phoneNo);
}
