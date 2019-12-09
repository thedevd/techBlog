package com.thedevd.springboot.controller;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.thedevd.springboot.bean.Person;
import com.thedevd.springboot.bean.SomeBean;

@RestController
public class FilteringController {

	@GetMapping("/filtering/static")
	public ResponseEntity<Person> staticFilteringDemo() {
		Person person = new Person("Ammy Jackson", 26, "123456");
		return ResponseEntity.status(HttpStatus.OK).body(person);
	}
	
	// include only property1 and property2 of SomeBean
	@GetMapping("/filtering/dynamic/property1and2")
	public ResponseEntity<MappingJacksonValue> dynamicFilteringDemo1(){
		SomeBean sbean = new SomeBean("property1", "property2", "property3");
		
		// SimpleBeanPropertyFilter.filterOutAllExcept() static method to construct filter that filters out all properties except ones specified in filterOutAllExcept()
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("property1","property2");
		
		SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter("SomeBeanFilter", filter); // note- filter id is very IMPORTANT
		
		MappingJacksonValue mapping = new MappingJacksonValue(sbean);
		mapping.setFilters(filterProvider); // telling the mappingJackson to use this filterProvider
		
		return ResponseEntity.status(HttpStatus.OK).body(mapping);
	}
	
	// include only property2 and property3 of SomeBean
	@GetMapping("/filtering/dynamic/property2and3")
	public ResponseEntity<MappingJacksonValue> dynamicFilteringDemo2(){
		List<SomeBean> sbeanList = Arrays.asList(new SomeBean("property1", "property2", "property3"),
				new SomeBean("property11", "property22", "property33"));
		
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("property2", "property3");
		
		SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter("SomeBeanFilter", filter); // note- filter id is very IMPORTANT
		
		MappingJacksonValue mapping = new MappingJacksonValue(sbeanList);
		mapping.setFilters(filterProvider);// telling the mappingJackson to use this filterProvider
		
		return ResponseEntity.status(HttpStatus.OK).body(mapping);
	}
}
