package com.thedevd.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.thedevd.springboot.bean.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

}
