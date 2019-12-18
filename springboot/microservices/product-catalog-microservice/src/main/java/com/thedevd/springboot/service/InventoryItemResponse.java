package com.thedevd.springboot.service;

public class InventoryItemResponse {

	private String productCode;
	private Integer availableQuantity;
	
	// read InventoryItem.java of inventory-service to know about why port was added
	private String port; 

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public Integer getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(Integer availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
}
