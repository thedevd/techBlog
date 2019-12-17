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
	private InventoryServiceProxy inventoryServiceFeignClient;
	
	public ProductCatalog getProductCatalogByProductCode( String productCode) {
		Optional<ProductCatalog> productCatalog = productCatalogRepository.findByProductCode(productCode);
		if(!productCatalog.isPresent()) {
			throw new ProductCatalogNotFoundException("Product not found with productCode: " + productCode);
		}
		
		// Get the availableQuantity of the product from inventory-service
		InventoryItemResponse inventoryResponse = inventoryServiceFeignClient.getInventoryByProductCode(productCode);
		productCatalog.get().setAvailableQuantity(inventoryResponse.getAvailableQuantity());
		
		return productCatalog.get();
	}
}
