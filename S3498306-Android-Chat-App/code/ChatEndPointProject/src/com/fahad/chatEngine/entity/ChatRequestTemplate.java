package com.fahad.chatEngine.entity;



public class ChatRequestTemplate {

	private String message,senderEmail,friendshipKey,token;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getFriendshipKey() {
		return friendshipKey;
	}

	public void setFriendshipKey(String friendshipKey) {
		this.friendshipKey = friendshipKey;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
	
}
