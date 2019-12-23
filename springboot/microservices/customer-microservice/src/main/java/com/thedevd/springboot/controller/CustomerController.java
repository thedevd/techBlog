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

import com.thedevd.springboot.entity.Customer;
import com.thedevd.springboot.exception.CustomerNotFoundException;
import com.thedevd.springboot.repository.CustomerJPARepository;

@RestController
public class CustomerController {

	@Autowired
	private CustomerJPARepository customerRepository;
	
	@GetMapping("/api/customer")
	public ResponseEntity<List<Customer>> getAllCustomer() {
		List<Customer> customers = customerRepository.findAll();
		return ResponseEntity.status(HttpStatus.OK).body(customers);
	}
	
	@PostMapping("/api/customer")
	public ResponseEntity<Customer> addNewCustomer(@RequestBody Customer customer) {
		Customer newCustomer = customerRepository.save(customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
	}
	
	@GetMapping("/api/customer/id/{id}")
	public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
		Optional<Customer> customer = customerRepository.findById(id);
		if(!customer.isPresent()) {
			throw new CustomerNotFoundException("Customer not found for id:" + id);
		}
		return ResponseEntity.status(HttpStatus.OK).body(customer.get());
	}
	
	@GetMapping("/api/customer/phone/{phoneNumber}")
	public ResponseEntity<Customer> getCustomerByPhoneNo(@PathVariable String phoneNumber) {
		Customer customer = customerRepository.findByPhoneNo(phoneNumber);
		if(customer == null) {
			 throw new CustomerNotFoundException("Customer not found for phoneNo:" + phoneNumber);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(customer);
	}
}
