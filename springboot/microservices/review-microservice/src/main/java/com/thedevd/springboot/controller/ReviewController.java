package com.thedevd.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.thedevd.springboot.entity.Review;
import com.thedevd.springboot.service.ReviewService;

@RestController
public class ReviewController {
	
	@Autowired
	private ReviewService reviewService;
	
	@GetMapping("/api/review/{productCode}")
	public ResponseEntity<List<Review>> getAllReviewsOfProduct(@PathVariable String productCode) {
		List<Review> reviews = reviewService.getReviewsByProductCode(productCode);
		return ResponseEntity.status(HttpStatus.OK).body(reviews);
	}
	
	@PostMapping("/api/review/")
	public ResponseEntity<Review> createReview(@RequestBody Review review) {
		Review createdReview = reviewService.addReview(review);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
	}
	
	@DeleteMapping("/api/review/{id}")
	public ResponseEntity<Review> deleteReview(@PathVariable Long id) {
		Review deletedReview = reviewService.deleteReview(id);
		return ResponseEntity.status(HttpStatus.OK).body(deletedReview);
	}

}
