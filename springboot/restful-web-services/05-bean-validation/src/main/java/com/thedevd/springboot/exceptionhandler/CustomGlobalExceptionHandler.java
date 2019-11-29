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

	/*
	 * Handle generic Exception.class
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAllException( Exception ex, WebRequest request ) {
		RestApiException apiError = new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
		return buildRestApiExceptionResponse(apiError);
	}
	
	/*
	 * Handle UserNotFoundException
	 */
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFoudException( Exception ex, WebRequest request ) {
		RestApiException apiError = new RestApiException(HttpStatus.NOT_FOUND, ex.getMessage());
		return buildRestApiExceptionResponse(apiError);
	}

	/* 
	 * Handle HttpMessageNotReadableException. Thrown when request JSON is malformed. 
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable( HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request )
	{
		RestApiException apiError = new RestApiException(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex);
		return buildRestApiExceptionResponse(apiError);
	}

	/*
	 * Handle MethodArgumentNotValidException, thrown when a bean fails @Valid validations
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid( MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request )
	{
		RestApiException apiError = new RestApiException(status, "Arguments validation failed", ex);
		return buildRestApiExceptionResponse(apiError);
	}
	
	private ResponseEntity<Object> buildRestApiExceptionResponse(RestApiException apiError) {
		return ResponseEntity.status(apiError.getStatus()).body(apiError);
	}
}
