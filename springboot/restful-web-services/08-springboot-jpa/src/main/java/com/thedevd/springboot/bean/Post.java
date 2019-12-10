package com.thedevd.springboot.bean;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Post {

	@Id
	@GeneratedValue
	private Integer postId;

	private String description;
	private LocalDateTime postedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore // do not want to include user details, otherwise there will be recursive retrieval of User and post within user.
	private User user;

	public Post()
	{
		super();
		this.postedAt = LocalDateTime.now();
	}

	public Post( String description )
	{
		this();
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public LocalDateTime getPostedAt()
	{
		return postedAt;
	}

	public void setPostedAt( LocalDateTime postedAt )
	{
		this.postedAt = postedAt;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser( User user )
	{
		this.user = user;
	}

	public Integer getPostId()
	{
		return postId;
	}

}
