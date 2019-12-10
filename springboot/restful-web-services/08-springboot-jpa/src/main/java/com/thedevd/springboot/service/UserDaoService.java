package com.thedevd.springboot.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.thedevd.springboot.bean.Post;
import com.thedevd.springboot.bean.User;
import com.thedevd.springboot.exception.UserNotFoundException;
import com.thedevd.springboot.repository.PostRepository;
import com.thedevd.springboot.repository.UserRepository;

@Service
public class UserDaoService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PostRepository postRepository;

	public List<User> findAll()
	{
		return userRepository.findAll();
	}

	public User findById( int id )
	{
		Optional<User> user = userRepository.findById(id);
		if( !user.isPresent() )
		{
			throw new UserNotFoundException("User not found with id: " + id);
		}
		return user.get();
	}

	public User deleteById( int id )
	{
		Optional<User> user = userRepository.findById(id);
		if( !user.isPresent() )
		{
			throw new UserNotFoundException("User not found with id: " + id);
		}
		userRepository.deleteById(id);
		return user.get();
	}

	public User saveUser( User user )
	{
		return userRepository.save(user);
	}
	
	public Post savePost(Post post) {
		return postRepository.save(post);
	}
}
