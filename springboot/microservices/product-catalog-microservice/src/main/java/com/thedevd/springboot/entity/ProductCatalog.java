package com.thedevd.springboot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "product_catalog")
public class ProductCatalog {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "product_code", unique = true, nullable = false)
	private String productCode;

	@Column(name = "product_name", nullable = false)
	private String productName;

	@Column(name = "product_description", columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "product_price")
	private Double price;

	@Transient
	private Integer availableQuantity;

	public ProductCatalog() {
		super();
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(Integer availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public Long getId() {
		return id;
	}

}
