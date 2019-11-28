package com.thedevd.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class I18NGreetingController {

	@Autowired
	private ResourceBundleMessageSource messageSource;
	
	@GetMapping("/greetme")
	public String sayGoodMorning() {
		return messageSource.getMessage("message.good.morning", null, LocaleContextHolder.getLocale());
	}
}
