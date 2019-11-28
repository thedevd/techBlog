package com.thedevd.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.thedevd.springboot.bean.HelloWorldBean;

@RestController
public class HelloworldController {

	@GetMapping( "hello-world" )
	public String helloWorld()
	{
		return "Hello world";
	}

	@GetMapping( "hello-world/{name}" )
	public String helloHuman( @PathVariable String name )
	{
		return "Hello " + name + ". Welcome to first spring boot restful service";
	}

	@GetMapping( "hello-world-bean" )
	public HelloWorldBean helloWorldBean()
	{
		return new HelloWorldBean("Welcome from HelloWorldBean");
	}
}
