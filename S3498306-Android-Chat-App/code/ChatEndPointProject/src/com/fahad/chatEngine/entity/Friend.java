package com.fahad.chatEngine.entity;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class Friend{
    @Persistent
    String User_Email;
	@Persistent
    String Friend_Email;
	@Persistent
    @PrimaryKey
	String key="";
	public Friend(String User_Email, String Friend_Email)
	{
		this.User_Email=User_Email.toLowerCase().trim().replace(" ", "");
		this.Friend_Email=Friend_Email.toLowerCase().trim().replace(" ", "");
		key=(this.User_Email+"::"+this.Friend_Email);
	}

	
	public Friend() {
		// TODO Auto-generated constructor stub
	}


	public String getKey() {
		return key;
	}
	public void setKey(String User_Email, String Friend_Email) {
		this.key=(User_Email+"::"+Friend_Email);
	}
	public String getUser_Email() {
		return User_Email;
	}
	public void setUser_Email(String user_Email) {
		User_Email = user_Email;
	}
	public String getFriend_Email() {
		return Friend_Email;
	}
	public void setFriend_Email(String friend_Email) {
		Friend_Email = friend_Email;
	}
    
	
}
