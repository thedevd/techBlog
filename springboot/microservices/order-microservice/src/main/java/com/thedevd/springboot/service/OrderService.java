package com.thedevd.springboot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thedevd.springboot.entity.Order;
import com.thedevd.springboot.exception.OrderNotFoundException;
import com.thedevd.springboot.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerServiceFeignClient customerServiceClient;

	public List<Order> getAllOrdersByCustomerPhoneNo(String customerPhoneNo) {
		List<Order> orders = orderRepository.findByCustomerPhoneNo(customerPhoneNo);

		if (!orders.isEmpty()) {
			// call the customer-service to get customer details
			CustomerDetailResponse customerDetailResponse = customerServiceClient
					.getCustomerDetailsByPhoneNo(customerPhoneNo);

			orders.stream().forEach(order -> {
				order.setCustomerName(customerDetailResponse.getName());
				order.setCustomerEmailId(customerDetailResponse.getEmailId());
				order.setCustomerAddress(customerDetailResponse.getPhoneNo());
			});

		}

		return orders;
	}

	public Order getOrderByOrderId(Long id) {
		Optional<Order> order = orderRepository.findById(id);
		if (!order.isPresent()) {
			throw new OrderNotFoundException("Order not found for order id:" + id);
		}

		// get the customer details from customer-service
		CustomerDetailResponse customerDetailResponse = customerServiceClient
				.getCustomerDetailsByPhoneNo(order.get().getCustomerPhoneNo());
		order.get().setCustomerName(customerDetailResponse.getName());
		order.get().setCustomerEmailId(customerDetailResponse.getEmailId());
		order.get().setCustomerAddress(customerDetailResponse.getAddress());

		return order.get();
	}

	public Order saveOrder(Order order) {
		
		order.getOrderItems().stream().forEach(orderItem -> orderItem.setOrder(order));
		Order newOrUpdatedOrder = orderRepository.save(order);

		// get the customer details from customer-service
		CustomerDetailResponse customerDetailResponse = customerServiceClient
				.getCustomerDetailsByPhoneNo(newOrUpdatedOrder.getCustomerPhoneNo());
		newOrUpdatedOrder.setCustomerName(customerDetailResponse.getName());
		newOrUpdatedOrder.setCustomerEmailId(customerDetailResponse.getEmailId());
		newOrUpdatedOrder.setCustomerAddress(customerDetailResponse.getAddress());
		
		return newOrUpdatedOrder;

	}

}
