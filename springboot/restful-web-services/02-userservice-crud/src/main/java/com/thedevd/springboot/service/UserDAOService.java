package com.thedevd.springboot.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import com.thedevd.springboot.bean.User;

@Service
public class UserDAOService {

	private static Map<Integer, User> users = new ConcurrentHashMap<Integer, User>();
	private AtomicInteger userCount = new AtomicInteger(3);
	static {
		/*
		 * java.util.Date - date + time + timezone
		 * java.time.LocalDate - only date
		 * 
		 * Java LocalDate is immutable class and hence thread safe.
		 * The LocalDate class has no time or Timezone data. So LocalDate is suitable to represent dates such as 
		 * Birthday, National Holiday etc.
		 */
		users.put(1, new User(1, "dev", LocalDate.of(1989, 12, 13)));
		users.put(2,new User(2, "ravi", LocalDate.of(1985, 12, 14)));
		users.put(3, new User(3, "ankit", LocalDate.of(1990, 12, 15)));
	}
	
	public Collection<User> findAll() {
		return users.values();
	}
	
	public User findById(int id) {
		return users.get(id);
	}
	
	public User save(User user) {
		if(user.getId() == null) {
			user.setId(userCount.incrementAndGet());
		}
		
		users.put(user.getId(), user);
		return user;
	}
	
	public User deleteById(int id) {
		if(users.containsKey(id)) {
			User removedUser = users.remove(id);
			return removedUser;
		}
		
		return null;
	}
}
