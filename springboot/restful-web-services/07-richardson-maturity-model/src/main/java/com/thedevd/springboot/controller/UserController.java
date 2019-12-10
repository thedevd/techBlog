package com.thedevd.springboot.controller;

import java.net.URI;
import java.util.Collection;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.thedevd.springboot.bean.User;
import com.thedevd.springboot.exception.UserNotFoundException;
import com.thedevd.springboot.service.UserDaoService;

@RestController
public class UserController {

	@Autowired
	UserDaoService userService;
	
	@GetMapping("/users")
	public ResponseEntity<Object> getAllUsers() {
		Collection<User> users = userService.findAll();
		return ResponseEntity.status(HttpStatus.OK).body(users);
	}
	
	@GetMapping("/users/{id}") 
	public ResponseEntity<Object> getUserById(@PathVariable int id) {
		User user = userService.findById(id);
		if(user == null) {
			throw new UserNotFoundException("User not found for id: " + id);
		}
		
		/* Creating HATEOS Response where we will also be providing RestURI for retrieving all users.*/
		EntityModel<User> entityModel = new EntityModel<User>(user);
		WebMvcLinkBuilder linkToAllUsers = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsers());
		entityModel.add(linkToAllUsers.withRel("all-users"));
		
		return ResponseEntity.status(HttpStatus.OK).body(entityModel);
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Object> deleteUserById(@PathVariable int id) {
		User deletedUser = userService.deleteById(id);
		if(deletedUser == null) {
			throw new UserNotFoundException("User not found for id: " + id);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(deletedUser);
	}
	
	// POST is used to create a new resource and then returns the resource URI
	@PostMapping("/users")
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
		User newUser = userService.saveOrUpdate(user);
		
		/* Creating HATEOAS response by including URI for retrieving all users and getting user by id */
		EntityModel<User> entityModel = new EntityModel<User>(newUser);
		WebMvcLinkBuilder linkToAllUsers = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsers());
		WebMvcLinkBuilder linkToSelf = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getUserById(newUser.getId()));
		
		entityModel.add(linkToAllUsers.withRel("all-users"));
		entityModel.add(linkToSelf.withRel("self"));
		
		URI newResourceURI = WebMvcLinkBuilder.linkTo(this.getClass())
				.toUriComponentsBuilder().path("/{id}")
				.buildAndExpand(newUser.getId()).toUri();
		
		// see sending 201 Created response Code when new resource is created - This is LEVEL 2 
		return ResponseEntity.status(HttpStatus.CREATED).location(newResourceURI).body(entityModel);
	}
	
	// PUT is used to replace a resource, if that resource  exist then simply update it, but if that resource doesn't exist then create it,
	@PutMapping("/users")
	public ResponseEntity<Object> updateUser(@Valid @RequestBody User user) {
		User newOrUpdatedUser = userService.saveOrUpdate(user);
		
		// HATEOAS response -- including uri for retrieve all users
		EntityModel<User> entityModel = new EntityModel<User>(newOrUpdatedUser);
		WebMvcLinkBuilder linkToAllUsers = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getAllUsers());
		entityModel.add(linkToAllUsers.withRel("all-users"));
		
		return ResponseEntity.status(HttpStatus.OK).body(entityModel);
	}
	
}
