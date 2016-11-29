package com.fahad.chatEngine.entity;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class User {
	
	@Persistent
	@PrimaryKey
	private String email=null;
	@Persistent
	private String Password;
	@Persistent
	@Unique
	private 	String token=null;
	@Persistent
	@Unique
	private String phone_number;
	@Persistent
	private String displayName;
	@Persistent
	private String profilePicture;
	@Persistent
	private boolean admin=false;
	
	public User()
	{
		
	}
	public User(String email, String password, String token, String phone_number, String displayName,
			String profilePicture, boolean admin) {
		super();
		this.email = email;
		Password = password;
		this.token = token;
		this.phone_number = phone_number;
		this.displayName = displayName;
		this.profilePicture = profilePicture;
		this.admin = admin;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	
}
