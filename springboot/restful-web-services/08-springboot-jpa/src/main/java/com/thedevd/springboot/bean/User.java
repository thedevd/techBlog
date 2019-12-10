package com.thedevd.springboot.bean;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

@Entity
public class User {

	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO) --> AUTO is the default strategy if strategy is not given
	@GeneratedValue
	private Integer id;

	@Size( min = 3, max = 20, message = "name should be 3 to 20 characters long" )
	private String name;

	@Past( message = "dob should be past date" )
	private LocalDate dob;

	@OneToMany( mappedBy = "user" ) // relate the Post to user by user property in Post.java.
	private List<Post> posts;

	public User()
	{
		// Default constructor is required by hibernate JPA
		super();
	}

	public User( String name, LocalDate dob )
	{
		super();
		this.name = name;
		this.dob = dob;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public LocalDate getDob()
	{
		return dob;
	}

	public void setDob( LocalDate dob )
	{
		this.dob = dob;
	}

	public Integer getId()
	{
		return id;
	}

	public List<Post> getPosts()
	{
		return posts;
	}

	public void setPosts( List<Post> posts )
	{
		this.posts = posts;
	}

}
