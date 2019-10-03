package com.example.demo;

import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

public class User {

	@Past
	private Date bDate;

	private int id;
	@Size(min =2,max = 33,message = "name should be 2 to 33 characters long")
	private String name;

	public int getId()
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

	public Date getbDate()
	{
		return bDate;
	}

	public void setbDate( Date bDate )
	{
		this.bDate = bDate;
	}
}
