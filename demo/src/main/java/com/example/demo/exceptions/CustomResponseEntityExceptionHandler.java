package com.example.demo.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler( Exception.class )
	public final ResponseEntity<Object> handleALlException( Exception ex, WebRequest request ) throws Exception
	{
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		ResponseEntity<Object> objectResponseEntity = new ResponseEntity(exceptionResponse,
				HttpStatus.INTERNAL_SERVER_ERROR);
		return objectResponseEntity;
	}

	@ExceptionHandler( UserNotFoundException.class )
	public final ResponseEntity<Object> handleUserNotFoundException( UserNotFoundException ex, WebRequest request )
			throws Exception
	{
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		ResponseEntity<Object> objectResponseEntity = new ResponseEntity(exceptionResponse, HttpStatus.NOT_FOUND);
		return objectResponseEntity;
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid( MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request )
	{
		ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), "Invalid Arguments",ex.getBindingResult().toString() );
		ResponseEntity<Object> objectResponseEntity = new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
		return objectResponseEntity;
	}
}
