package com.thedevd.springboot.exception;

@SuppressWarnings("serial")
public class ProductCatalogNotFoundException extends RuntimeException {

	public ProductCatalogNotFoundException(String message) {
		super(message);
	}
	
}
