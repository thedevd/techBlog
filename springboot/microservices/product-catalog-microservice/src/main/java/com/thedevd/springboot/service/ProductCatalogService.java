package com.thedevd.springboot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thedevd.springboot.entity.ProductCatalog;
import com.thedevd.springboot.exception.ProductCatalogNotFoundException;
import com.thedevd.springboot.repository.ProductCatalogRepository;

@Service
public class ProductCatalogService {

	@Autowired
	private ProductCatalogRepository productCatalogRepository;
	
	@Autowired
	private InventoryServiceFeignClient inventoryServiceFeignClient;
	
	public ProductCatalog getProductCatalogByProductCode( String productCode) {
		Optional<ProductCatalog> productCatalog = productCatalogRepository.findByProductCode(productCode);
		if(!productCatalog.isPresent()) {
			throw new ProductCatalogNotFoundException("Product not found with productCode: " + productCode);
		}
		
		// Get the availableQuantity of the product from inventory-service
		InventoryItemResponse inventoryResponse = inventoryServiceFeignClient.getInventoryByProductCode(productCode);
		productCatalog.get().setAvailableQuantity(inventoryResponse.getAvailableQuantity());
		
		// also include the port to know which instance of inventory-service has returned the availableQuantity.
		// this is done just for demo purpose of Ribbon (load balancer). (Do not do this in production)
		productCatalog.get().setInventoryServicePort(inventoryResponse.getPort());
		
		return productCatalog.get();
	}
}
