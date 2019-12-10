package com.thedevd.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.thedevd.springboot.bean.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
