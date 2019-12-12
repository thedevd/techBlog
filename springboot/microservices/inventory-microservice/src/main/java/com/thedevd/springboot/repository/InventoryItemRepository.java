package com.thedevd.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thedevd.springboot.entity.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
	Optional<InventoryItem> findByProductCode(String productCode);
	List<InventoryItem> findByAvailableQuantityLessThanEqual(int quantity);
}
