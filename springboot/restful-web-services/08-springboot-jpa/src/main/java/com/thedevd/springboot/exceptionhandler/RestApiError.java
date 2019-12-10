package com.thedevd.springboot.exceptionhandler;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class RestApiError {

	private LocalDateTime timstamp;
	private HttpStatus status;
	private String message;
	private String debugMessage;

	public RestApiError()
	{
		super();
		this.timstamp = LocalDateTime.now();
	}

	public RestApiError( HttpStatus status, String message )
	{
		this();
		this.status = status;
		this.message = message;
	}

	public RestApiError( HttpStatus status, String message, Throwable ex )
	{
		this(status, message);
		this.debugMessage = ex.getLocalizedMessage();
	}

	public LocalDateTime getTimstamp()
	{
		return timstamp;
	}

	public void setTimstamp( LocalDateTime timstamp )
	{
		this.timstamp = timstamp;
	}

	public HttpStatus getStatus()
	{
		return status;
	}

	public void setStatus( HttpStatus status )
	{
		this.status = status;
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
