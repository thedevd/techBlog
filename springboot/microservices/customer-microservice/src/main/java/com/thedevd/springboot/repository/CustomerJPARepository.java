package com.thedevd.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thedevd.springboot.entity.Customer;

@Repository
public interface CustomerJPARepository extends JpaRepository<Customer, Long> {

	public Customer findByPhoneNo(String phoneNo);
}
