package com.thedevd.springboot.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.thedevd.springboot.entity.InventoryItem;
import com.thedevd.springboot.repository.InventoryItemRepository;

@RestController
public class InventoryItemController {

	@Autowired
	private InventoryItemRepository inventoryItemRepository;

	@GetMapping("/api/inventory")
	public ResponseEntity<List<InventoryItem>> getAllInventory() {
		List<InventoryItem> inventory = inventoryItemRepository.findAll();
		return ResponseEntity.status(HttpStatus.OK).body(inventory);
	}

	@GetMapping("/api/inventory/{productCode}")
	public ResponseEntity<InventoryItem> getInventoryByProductCode(@PathVariable String productCode) {
		Optional<InventoryItem> inventoryItem = inventoryItemRepository.findByProductCode(productCode);
		if (inventoryItem.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(inventoryItem.get());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@GetMapping("/api/inventory/outofstock")
	public ResponseEntity<List<InventoryItem>> getOutOfStockInventory() {
		List<InventoryItem> outOfStockInventory = inventoryItemRepository.findByAvailableQuantityLessThanEqual(0);
		return ResponseEntity.status(HttpStatus.OK).body(outOfStockInventory);
	}
	
	@PostMapping("/api/inventory")
	public ResponseEntity<InventoryItem> addNewInventory(@RequestBody InventoryItem invItem) {
		InventoryItem newInventory = inventoryItemRepository.save(invItem);
		return ResponseEntity.status(HttpStatus.CREATED).body(newInventory);
	}
	
}
