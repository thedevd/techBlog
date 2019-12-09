package com.thedevd.springboot.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Person {

	private String name;
	private int age;
	
	@JsonIgnore // static filtering
	private String ssn;

	public Person()
	{
		super();
	}

	public Person( String name, int age, String ssn )
	{
		super();
		this.name = name;
		this.age = age;
		this.ssn = ssn;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge( int age )
	{
		this.age = age;
	}

	public String getSsn()
	{
		return ssn;
	}

	public void setSsn( String ssn )
	{
		this.ssn = ssn;
	}

}
