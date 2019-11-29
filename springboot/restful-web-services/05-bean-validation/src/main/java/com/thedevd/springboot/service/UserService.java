package com.thedevd.springboot.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import com.thedevd.springboot.bean.User;

@Service
public class UserService {

	private static Map<Integer, User> users = new ConcurrentHashMap<>();
	private static AtomicInteger userCounts = new AtomicInteger();
	
	static {
		users.put(1, new User(1, "dev", LocalDate.of(1989, 12, 13)));userCounts.incrementAndGet();
		users.put(2, new User(2, "ravi", LocalDate.of(1985, 12, 14)));userCounts.incrementAndGet();
		users.put(3, new User(3, "ankit", LocalDate.of(1990, 12, 15)));userCounts.incrementAndGet();
	}
	
	public Collection<User> findAll() {
		return users.values();
	}
	
	public User findById(int id) {
		return users.get(id);
	}
	
	public User deleteById(int id) {
		User removedUser = null;
		if(users.containsKey(id)) {
			removedUser = users.remove(id);
		}
		return removedUser;
	}
	
	public User save(User user) {
		if(user.getId() == null) {
			user.setId(userCounts.incrementAndGet());
		}
		users.put(user.getId(), user);
		return user;
	}
	
}
