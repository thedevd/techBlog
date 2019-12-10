package com.thedevd.springboot.exceptionhandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.thedevd.springboot.exception.UserNotFoundException;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/* Handle generic Exception.class */
	@ExceptionHandler( Exception.class )
	public ResponseEntity<Object> handleAllException( Exception ex, WebRequest request )
	{
		RestApiError apiError = new RestApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
		return buildRestApiErrorResponse(apiError);
	}

	/* Handle UserNotFoundException. Thrown when user does not exist. */
	@ExceptionHandler( UserNotFoundException.class )
	public ResponseEntity<Object> handleUserNotFoundException( UserNotFoundException ex, WebRequest request )
	{
		RestApiError apiError = new RestApiError(HttpStatus.NOT_FOUND, ex.getMessage());
		return buildRestApiErrorResponse(apiError);
	}

	/* Handle HttpMessageNotReadableException. Thrown when request JSON is malformed. */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable( HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request )
	{
		RestApiError apiError = new RestApiError(HttpStatus.BAD_REQUEST, "JSON is malformed", ex);
		return buildRestApiErrorResponse(apiError);
	}
	
	/*
	 * Handle MethodArgumentNotValidException, thrown when a bean fails @Valid validations
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid( MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request )
	{
		RestApiError apiError = new RestApiError(status, "Arguments validation failed", ex);
		return buildRestApiErrorResponse(apiError);
	}

	private ResponseEntity<Object> buildRestApiErrorResponse( RestApiError apiError )
	{
		return ResponseEntity.status(apiError.getStatus()).body(apiError);
	}

}
