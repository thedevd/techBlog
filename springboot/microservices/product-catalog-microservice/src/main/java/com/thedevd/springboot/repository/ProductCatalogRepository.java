package com.thedevd.springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thedevd.springboot.entity.ProductCatalog;

@Repository
public interface ProductCatalogRepository extends JpaRepository<ProductCatalog, Long> {
	Optional<ProductCatalog> findByProductCode(String productCode);
}
