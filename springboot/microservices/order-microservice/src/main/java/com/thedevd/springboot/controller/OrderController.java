package com.thedevd.springboot.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.thedevd.springboot.entity.Order;
import com.thedevd.springboot.entity.OrderItem;
import com.thedevd.springboot.service.OrderService;

@RestController
public class OrderController {

	@Autowired
	private OrderService orderService;

	@GetMapping("/api/order/id/{id}")
	public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
		Order order = orderService.getOrderByOrderId(id);
		return ResponseEntity.status(HttpStatus.OK).body(order);
	}

	@GetMapping("/api/order/phone/{phoneNo}")
	public ResponseEntity<List<Order>> getAllOrderByCustomerPhoneNo(@PathVariable String phoneNo) {
		List<Order> ordersOfCustomer = orderService.getAllOrdersByCustomerPhoneNo(phoneNo);
		return ResponseEntity.status(HttpStatus.OK).body(ordersOfCustomer);
	}

	@PostMapping("/api/order")
	public ResponseEntity<Order> createOrder(@RequestBody Order order) {
		// calculate the totalAmount of order
		Double totalOrderAmount = order.getOrderItems().stream()
				.collect(Collectors.summingDouble(OrderItem::getTotalPrice));
		order.setOrderAmount(totalOrderAmount);
		
		Order newOrder = orderService.saveOrder(order);
		return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
	}
}
