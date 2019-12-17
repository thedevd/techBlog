package com.thedevd.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.thedevd.springboot.entity.ProductCatalog;
import com.thedevd.springboot.service.ProductCatalogService;

@RestController
public class ProductCatalogController {

	@Autowired
	private ProductCatalogService productCatalogService;
	
	@GetMapping("/api/product/{productCode}")
	public ResponseEntity<ProductCatalog> getProductByProductCode(@PathVariable String productCode) {
		ProductCatalog product = productCatalogService.getProductCatalogByProductCode(productCode);
		return ResponseEntity.status(HttpStatus.OK).body(product);
	}
}
