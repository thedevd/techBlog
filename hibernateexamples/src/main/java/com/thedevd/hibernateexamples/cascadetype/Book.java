package com.thedevd.hibernateexamples.cascadetype;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
class Book {

	@Id
	private Integer id;
	
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
