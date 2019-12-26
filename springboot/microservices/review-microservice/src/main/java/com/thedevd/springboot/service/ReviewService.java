package com.thedevd.springboot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thedevd.springboot.entity.Review;
import com.thedevd.springboot.repository.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;
	
	public List<Review> getReviewsByProductCode(String productCode) {
		return reviewRepository.findByProductCode(productCode);
	}
	
	public Review addReview(Review review) {
		return reviewRepository.save(review);
	}
	
	public Review deleteReview(Long id) {
		Optional<Review> review = reviewRepository.findById(id);
		if(!review.isPresent()) {
			throw new RuntimeException("Review not found with id: " + id);
		}
		reviewRepository.deleteById(id);
		return review.get();
	}
	
}
