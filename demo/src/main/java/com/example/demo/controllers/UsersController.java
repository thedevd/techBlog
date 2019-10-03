package com.example.demo.controllers;

import com.example.demo.User;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class UsersController {

	@Autowired private UserService userService;

	@PostMapping( "/yellow/{lastName}" )
	public String naam( @PathVariable String lastName )
	{
		return "dev" + ":" + ":" + lastName;
	}

	@GetMapping( "/users" )
	public List<User> datetest()
	{
		return userService.getAllUsers();
	}

	@PostMapping( path = "/users" )
	public ResponseEntity<User> createUser(@Valid @RequestBody User newUSer )
	{
		User user = userService.addUser(newUSer);
		URI loc = ServletUriComponentsBuilder.fromCurrentContextPath().path("{id}").buildAndExpand(user.getId())
				.toUri();
		return ResponseEntity.created(loc).build();
	}

	@GetMapping( "/users/{id}" )
	public User findOneUser( @PathVariable int id )
	{
		if(id <=-1){
			throw new RuntimeException();
		}
		User user = userService.findOneUser(id);
		if( user == null )
		{
			throw new UserNotFoundException("id:" + id);
		}
		return user;
	}

	@DeleteMapping("/users/{id}")
	public void deleteOneUser( @PathVariable int id )
	{
		boolean b = userService.deleteUser(id);
		if(!b){
			throw new UserNotFoundException("id:"+id);
		}
	}

}
