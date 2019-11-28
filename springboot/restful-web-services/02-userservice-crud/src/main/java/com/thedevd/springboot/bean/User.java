package com.thedevd.springboot.bean;

import java.time.LocalDate;

public class User {

	private Integer id;
	private String name;
	private LocalDate dob;

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
