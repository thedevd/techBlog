package com.example.demo.services;

import com.example.demo.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class UserService {

	private int userCounter = 0;
	private static List<User> storedUsers = new ArrayList<>();

	static {

		User e = new User();
		e.setId(0);
		e.setName("dev");
		e.setbDate(new Date());
		storedUsers.add(e);
	}
	public User addUser( User user )
	{
		user.setId(++userCounter);
		storedUsers.add(user);
		return user;
	}

	public List<User> getAllUsers()
	{
		return storedUsers;
	}

	public User findOneUser( int id )
	{
		for( User storedUser : storedUsers )
		{
			if (storedUser.getId() ==id){
				return storedUser;
			}
		}
		return null;
	}

	public boolean deleteUser( int id )
	{
		Iterator<User> iterator = storedUsers.iterator();
		while( iterator.hasNext() ){
			User next = iterator.next();
			if(next.getId() ==id){
				iterator.remove();
				return true;
			}
		}
		return false;
	}
}
