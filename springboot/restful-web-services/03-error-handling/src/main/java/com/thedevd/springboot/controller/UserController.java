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
import com.thedevd.springboot.exception.UserNotFoundException;
import com.thedevd.springboot.service.UserDAOService;

@RestController
public class UserController {

	@Autowired
	UserDAOService userService;

	@GetMapping( "/users" )
	public ResponseEntity<Collection<User>> getAllUser()
	{
		Collection<User> users = userService.findAll();
		return ResponseEntity.status(HttpStatus.OK).body(users);
	}

	@GetMapping( "/users/{id}" )
	public ResponseEntity<Object> getUserById( @PathVariable int id )
	{
		User user = userService.findById(id);
		if( user == null )
		{
			throw new UserNotFoundException("User not found with id: " + id);
		}
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	@DeleteMapping( "/users/{id}" )
	public ResponseEntity<Object> deleteUserById( @PathVariable int id )
	{
		User user = userService.deleteById(id);
		if( user == null )
		{
			throw new UserNotFoundException("User not found with id: " + id);
		}
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	@PostMapping( "/users/save" )
	public ResponseEntity<Object> saveUser( @RequestBody User user )
	{
		User userSaved = userService.save(user);
		return ResponseEntity.status(HttpStatus.OK).body(userSaved);
	}
	
	// This rest is just to show the demo of ExceptionHandler for Exception.class
	// which is handled by handleAllException() method of CustomExceptionHandler
	@PostMapping( "/users/save2" )
	public ResponseEntity<Object> saveUserWithThrowingException( @RequestBody User user )
	{
		if(user.getId()==null || user.getDob() == null) {
			throw new RuntimeException("User Id or dob can not be null");
		}
		User userSaved = userService.save(user);
		return ResponseEntity.status(HttpStatus.OK).body(userSaved);
	}
}
