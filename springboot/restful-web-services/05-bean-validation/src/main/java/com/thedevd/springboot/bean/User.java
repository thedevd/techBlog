package com.thedevd.springboot.bean;

import java.time.LocalDate;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

public class User {

	private Integer id;
	
	@Size(min=3, max=20, message="name should be 3 to 20 characters long") // bean validation
	private String name;
	
	@Past(message = "dob should be past date") // bean validation
	private LocalDate dob;
	
	/*
	 * @Min(value = 18, message = "Age should not be less than 18")
	 * @Max(value = 100, message = "Age should not be greater than 100")
	 * private int age;
    */

	public User( Integer id, String name, LocalDate dob )
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

	public void setId( Integer id )
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
