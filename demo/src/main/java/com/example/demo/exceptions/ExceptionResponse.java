package com.example.demo.exceptions;

import java.util.Date;

public class ExceptionResponse {

	private final String description;
	private final String message;
	private final Date date;

	public ExceptionResponse( Date date, String message, String description )
	{
		this.date = date;
		this.message = message;
		this.description= description;
	}

	public String getDescription()
	{
		return description;
	}

	public String getMessage()
	{
		return message;
	}

	public Date getDate()
	{
		return date;
	}
}
