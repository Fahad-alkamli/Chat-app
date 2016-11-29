package com.fahad.chatEngine.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class Chat {

	@Persistent
	private String message,senderEmail,friendshipKey;
	@Persistent
	private String sendDateTime;
    @PrimaryKey
	@Persistent
	private String key="";
	
    private boolean IsRead=false;
	public Chat(String message, String senderEmail, String friendshipKey) 
	{
		this.message = message;
		this.senderEmail = senderEmail;
		this.friendshipKey = friendshipKey;
		  String timeStamp = new SimpleDateFormat("dd/M/yyyy HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
		this.sendDateTime = timeStamp;	
		this.key=this.friendshipKey+"::"+this.sendDateTime;
		this.IsRead=false;
		
		
	}
	public Chat(Chat chat) 
	{
		this.message = chat.getMessage();
		this.senderEmail = chat.getSenderEmail();
		this.friendshipKey = chat.getFriendshipKey();
		this.sendDateTime = chat.getSendDateTime();
		this.key=this.friendshipKey+"::"+this.sendDateTime;
		this.IsRead=chat.isIsRead();
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		if(key=="" || key==null)
		{
			return this.friendshipKey+"::"+this.sendDateTime;
		}
		return key;
	}

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
	public String getSendDateTime() {
		return sendDateTime;
	}
	public void setSendDateTime(String sendDateTime) {
		this.sendDateTime = sendDateTime;
	}

	public boolean isIsRead() {
		return IsRead;
	}

	public void setIsRead(boolean isRead) {
		IsRead = isRead;
	}
	
	 
}
