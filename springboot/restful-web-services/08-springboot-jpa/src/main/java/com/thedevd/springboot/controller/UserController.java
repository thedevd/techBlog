package com.thedevd.springboot.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.thedevd.springboot.bean.Post;
import com.thedevd.springboot.bean.User;
import com.thedevd.springboot.service.UserDaoService;

@Controller
public class UserController {

	@Autowired
	UserDaoService userService;
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.findAll();
		return ResponseEntity.status(HttpStatus.OK).body(users);
	}
	
	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable int id) {
		User user = userService.findById(id);
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<User> deleteUserById(@PathVariable int id) {
		User deletedUser = userService.deleteById(id);
		return ResponseEntity.status(HttpStatus.OK).body(deletedUser);
	}
	
	@PostMapping("/users")
	public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
		User newUser = userService.saveUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
	}
	
	@PutMapping("/users") 
	public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
		// validate first that user exists
		userService.findById(user.getId());
		
		User updatedUser = userService.saveUser(user);
		return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
	}
	
	@PostMapping("/users/{id}/posts")
	public ResponseEntity<Post> createPost(@PathVariable int id, @RequestBody Post post) {
		// first find user of the id
		User user = userService.findById(id);
		post.setUser(user);
		
		Post savedPost = userService.savePost(post);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
	}
	
	@GetMapping("/users/{id}/posts")
	public ResponseEntity<List<Post>> getAllPostsOfUser(@PathVariable int id) {
		User user = userService.findById(id);
		List<Post> posts = user.getPosts();
		
		return ResponseEntity.status(HttpStatus.OK).body(posts);
	}
}
