package com.fahad.chatEngine.entity;

public class CheckNumberRequst {
	private String token;
	private String userEmail;
	private String phoneNumbers[];
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String[] getPhoneNumbers() {
		return phoneNumbers;
	}
	public void setPhoneNumbers(String[] phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	

}
