package com.mygene2.postgreslistener.model;

import java.util.Date;

/**
 * Object to store Account information .<br>
 * The password will be destroyed upon successfully logging in and will NOT be
 * passed around on subsequent requests
 *
 * @author lfallon
 *
 */
public class Account {

	private Integer id;
	private String email;
	private String username;
	private String firstName;
	private String lastName;
	private Date dateCreated;
	private Boolean isClinician;

	public Account() {
	}

	public Account(Integer id, String email, String username, String firstName,
			String lastName, Date dateCreated, Boolean isClinician) {
		this.id = id;
		this.email = email;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateCreated = dateCreated;
		this.isClinician = isClinician;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Boolean getIsClinician() {
		return isClinician;
	}

	public void setIsClinician(Boolean isClinician) {
		this.isClinician = isClinician;
	}

}
