package com.thedevd.springboot.bean;

import java.time.LocalDate;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

public class User {

	private Integer id;
	
	@Size(min = 2, max = 20, message = "name should be 3 to 20 chacters long")
	private String name;
	
	@Past(message = "DOB should be past date")
	private LocalDate dob;

	public User()
	{
		super();
	}

	public User( int id, String name, LocalDate dob )
	{
		super();
		this.id = id;
		this.name = name;
		this.dob = dob;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
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

}
