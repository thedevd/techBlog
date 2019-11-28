package com.thedevd.springboot.controller;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.thedevd.springboot.bean.User;
import com.thedevd.springboot.service.UserDAOService;

@RestController
public class UserController {

	@Autowired
	UserDAOService userService;
	
	@GetMapping("/users")
	public ResponseEntity<Collection<User>> getAllUsers() {
		return ResponseEntity.ok().body(userService.findAll());
		// return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
	}
	
	@GetMapping("/users/{id}")
	public ResponseEntity<Object> getUserById(@PathVariable int id) {
		User user = userService.findById(id);
		if(user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with Id: " + id);
		}
		return ResponseEntity.status(HttpStatus.OK).body(user);
		// return ResponseEntity.ok().body(user);
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Object> deleteUserById(@PathVariable int id) {
		User user = userService.deleteById(id);
		if(user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with Id: " + id);
		}
		return ResponseEntity.status(HttpStatus.OK).body(user);
		// return ResponseEntity.ok().body(user);
	}
	
	@PostMapping("/users/save")
	public ResponseEntity<Object> saveUser(@RequestBody User user) {
		userService.save(user);
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}
}
