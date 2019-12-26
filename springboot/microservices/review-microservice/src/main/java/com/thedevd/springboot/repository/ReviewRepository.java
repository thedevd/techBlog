package com.thedevd.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thedevd.springboot.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	public List<Review> findByProductCode(String productCode);
	
}
