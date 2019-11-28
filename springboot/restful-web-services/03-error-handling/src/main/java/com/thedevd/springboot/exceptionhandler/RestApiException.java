package com.thedevd.springboot.exceptionhandler;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class RestApiException {

	private HttpStatus status; // holds the HTTP call response status
	private LocalDateTime timestamp; // holds the date-time instance of when the error happened.
	private String message; // holds a user-friendly message about the error.
	private String debugMessage; // holds message describing the error in more detail for debug.

	public RestApiException()
	{
		this.timestamp = LocalDateTime.now();
	}

	public RestApiException( HttpStatus status, String message )
	{
		this(); // to set timestamp
		this.status = status;
		this.message = message;
	}

	public RestApiException( HttpStatus status, String message, Throwable ex )
	{
		this(); // to set timestamp
		this.status = status;
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
	}

	public HttpStatus getStatus()
	{
		return status;
	}

	public void setStatus( HttpStatus status )
	{
		this.status = status;
	}

	public LocalDateTime getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp( LocalDateTime timestamp )
	{
		this.timestamp = timestamp;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage( String message )
	{
		this.message = message;
	}

	public String getDebugMessage()
	{
		return debugMessage;
	}

	public void setDebugMessage( String debugMessage )
	{
		this.debugMessage = debugMessage;
	}

}
