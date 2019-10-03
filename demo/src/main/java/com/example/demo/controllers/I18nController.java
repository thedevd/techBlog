package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class I18nController {
	@Autowired
	private ResourceBundleMessageSource messageSource;

	@GetMapping("/greetme")
	public String morningGreeting1(){
		return messageSource.getMessage("message.good.morning",null, LocaleContextHolder.getLocale());
	}


}
