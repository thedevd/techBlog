package com.thedevd.springboot.exceptionhandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.thedevd.springboot.exception.RestApiException;
import com.thedevd.springboot.exception.UserNotFoundException;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	/*
	 * Handle generic Exception.class
	 */
	@ExceptionHandler( Exception.class )
	public final ResponseEntity<Object> handleAllException( Exception ex, WebRequest request ) throws Exception
	{
		RestApiException apiError = new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
		return buildResponseEntity(apiError);
	}

	/* 
	 * Handle UserNotFoundException. Thrown user does not exist. 
	 */
	@ExceptionHandler( UserNotFoundException.class )
	public final ResponseEntity<Object> handleUserNotFoundException( UserNotFoundException ex, WebRequest request )
	{
		RestApiException apiError = new RestApiException(HttpStatus.NOT_FOUND, ex.getMessage());
		return buildResponseEntity(apiError);
	}

	/* 
	 * Handle HttpMessageNotReadableException. Thrown when request JSON is malformed. 
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable( HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request )
	{
		RestApiException apiError = new RestApiException(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex);
		return buildResponseEntity(apiError);
	}

	private ResponseEntity<Object> buildResponseEntity( RestApiException apiError )
	{
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}

}
