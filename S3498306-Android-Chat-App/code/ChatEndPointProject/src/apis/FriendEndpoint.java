package apis;

import com.fahad.chatEngine.entity.CheckNumberRequst;
import com.fahad.chatEngine.entity.Friend;
import com.fahad.chatEngine.entity.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

@Api(name = "friendendpoint", namespace = @ApiNamespace(ownerDomain = "fahad.com", ownerName = "fahad.com", packagePath = "chatEngine.entity"))
public class FriendEndpoint {

	
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listFriend")
	public CollectionResponse<Friend> listFriend(@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Friend> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Friend.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Friend>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Friend obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Friend> builder().setItems(execute).setNextPageToken(cursorString).build();
	}


	@ApiMethod(name = "insertFriend")
	public Friend insertFriend(Friend friend,@Named("token") String token) 
	{
		PersistenceManager mgr = getPersistenceManager();
		try {
			//Check if this is a phone number or an email
			if(!friend.getFriend_Email().contains("@"))
			{
				//This is a phone number
				String friendEmail=friendExists(friend.getFriend_Email(),friend.getUser_Email());
				if(friendEmail == null)
				{
					throw new EntityExistsException("Phone number doesn't exists");
				}
				
				friend.setFriend_Email(friendEmail);
			}
			
			friend.setKey(friend.getUser_Email(),friend.getFriend_Email());
			if (containsFriend(friend)) {
				throw new EntityExistsException("Object already exists");
			}else if(friend.getUser_Email() ==null || friend.getFriend_Email() ==null)
			{
				throw new EntityExistsException("requird fields missing.");
			}
			User user=null;	
			User userFriend=null;
			try{
				user=mgr.getObjectById(User.class,friend.getUser_Email());
				userFriend=mgr.getObjectById(User.class,friend.getFriend_Email());
				if(user == null || userFriend ==null)
				{
					throw new  EntityNotFoundException("user email or friend email doesn't exists");
					
				}
			}catch(Exception e)
			{
				throw new  EntityNotFoundException("user email or friend email doesn't exists");
			}
			if(!validateUserAndToken(friend.getUser_Email(),token))
			{
				throw new  EntityNotFoundException("token mismatch");
			}
			//Add him to the friend's list
			Friend friend2=new Friend(friend.getFriend_Email(),friend.getUser_Email());
			if(!containsFriend(friend2))
			{
				mgr.makePersistent(friend2);
			}
			
			mgr.makePersistent(friend);
		} finally {
			mgr.close();
		}
		return friend;
	}

	@ApiMethod(name = "removeFriend")
	public void removeFriend(@Named("UserEmail") String UserEmail,@Named("friendEmail")String friendEmail,@Named("token") String token) {
		PersistenceManager mgr = getPersistenceManager();
		
		try {
			
			if(!validateUserAndToken(UserEmail,token))
			{
				throw new 	EntityExistsException("not valid token or username");
			}
			Friend friend=new Friend(UserEmail,friendEmail);
			
			if(!containsFriend(friend))
			{
				throw new 	EntityExistsException("this friend doesn't exists in the database");	
			}
			 friend = mgr.getObjectById(Friend.class, friend.getKey());
			 mgr.deletePersistent(friend);
			 //Also delete himself from his friend list
			 friend=new Friend(friendEmail,UserEmail);
			 friend = mgr.getObjectById(Friend.class, friend.getKey());
			 mgr.deletePersistent(friend);
				
		} finally {
			mgr.close();
		}
	}

	protected static boolean containsFriend(Friend friend) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Friend.class, friend.getKey());
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			contains = false;
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

	
	private boolean validateUserAndToken(String user,String token)
	{
		User user2=null;
		PersistenceManager mgr = getPersistenceManager();
		try{
		user2=	mgr.getObjectById(User.class,user);
		if(user == null)
		{
		return false;
		}
		//Validate the token
		if(user2.getToken().trim().equals(token.trim()) != true)
		{
			return false;
		}
		}catch(Exception e)
		{
			return false;	
		}
		return true;
	}
	
	@ApiMethod(name = "getFriends")
	public CollectionResponse<User> getFriends(@Named("UserEmail") String UserEmail,@Named("token") String token ) 
	{
		
		PersistenceManager mgr = null;
		List<Friend> execute = null;
		List<User> friends = new ArrayList<User>();
		try {
			//validate the user email and token
			mgr = getPersistenceManager();
		
			if(validateUserAndToken(UserEmail,token)==false)
			{
			throw new 	EntityExistsException("doesn't exists");
			}			
			//end
			
			Query query = mgr.newQuery(Friend.class);
			query.setFilter("User_Email == UserEmailPar");
			query.declareParameters("String UserEmailPar");
			execute = (List<Friend>) query.execute(UserEmail);

			//Fetch friend's details  for now his name 
			for(Friend friend:execute)
			{
			try{
				User user=mgr.getObjectById(User.class,friend.getFriend_Email());
				User user2=new User();
				user2.setDisplayName(user.getDisplayName());
				user2.setEmail(user.getEmail());
				if(user.getProfilePicture() != null)
				{
					user2.setProfilePicture(user.getProfilePicture());
				}
				friends.add(user2);
			}catch(Exception e)
			{
				
			}
			}

		} finally {
			mgr.close();
		}

		return CollectionResponse.<User> builder().setItems(friends).build();
	
	}

	
	protected static String friendExists(String phoneNumber,String userEmail)
	{
		PersistenceManager mgr = null;
		List<User> execute = null;
		mgr = getPersistenceManager();
		if(phoneNumber==null)
		{
			return null;
		}
			if (phoneNumber.length()>=9 && phoneNumber.contains("@")==false)
			{
				//prepare the phone number for search in the database get the last 9digits
				String tempPhone=phoneNumber.substring(phoneNumber.length()-9);

				Query query = mgr.newQuery(User.class);
				execute = (List<User>) query.execute();
	
				for(User user:execute)
				{
		        	if(user.getPhone_number().substring(user.getPhone_number().length()-9).contains(tempPhone) && user.getEmail().contains(userEmail)==false)
		        	{
		        		User user2=new User();
		        		user2.setEmail(user.getEmail());
		        	    	return user2.getEmail();
		        	}
				}
				
				
			}else{
				//This is an email so check for email in the database
				if(UserEndpoint.getUser(phoneNumber)!= null)
				{
					return UserEndpoint.getUser(phoneNumber).getEmail();
				}
			}
			
			return null;
	}
		
		
	@ApiMethod(name = "friendsExists")
	public User[] friendsExists(CheckNumberRequst friend) 
	{
		ArrayList<User> friends=new ArrayList<User>();
		if(friend ==null || friend.getPhoneNumbers().length<1)
		{
			throw new 	EntityNotFoundException("all information should be filled");
		}
		if(!validateUserAndToken(friend.getUserEmail(),friend.getToken()))
		{
			throw new 	EntityNotFoundException("token or email mismatch");	
		}
	
		for(String number:friend.getPhoneNumbers())
		{
			User user=getFriend(number,friend.getUserEmail());
			if(user != null)
			{
				friends.add(user);
			}
		}
		if(friends.size()<1)
		{
			throw new 	EntityNotFoundException("no number exists in the database");	
		}
		
		//throw new 	EntityNotFoundException("no number exists in the database:"+Integer.toString(friends.size()));	
		return friends.toArray(new User[friends.size()]);
		
			
	}
	
	
	private User getFriend(String phoneNumber,String userEmail)
	{
		
		if (phoneNumber.length()>=9 && phoneNumber.contains("@")==false)
		{
			//prepare the phone number for search in the database get the last 9digits
			String tempPhone=phoneNumber.substring(phoneNumber.length()-9);
			PersistenceManager mgr = null;
			List<User> execute = null;
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(User.class);
			execute = (List<User>) query.execute();
			
			for(User user:execute)
			{
	        	if(user.getPhone_number().substring(user.getPhone_number().length()-9).contains(tempPhone) && user.getEmail().contains(userEmail)==false)
	        	{
	        		Friend friend=new Friend();
	        		friend.setKey(userEmail, user.getEmail());
	        		//Just making sure that if they are already friends there is no need for returning the number to the user
	        		if (containsFriend(friend)) 
	        		{
	    				return null;
	    			}
	        		
	        		User user2=new User();
	        		user2.setEmail(user.getEmail());
	        		user2.setPhone_number(user.getPhone_number());
	        	    	return user2;
	        	}
			}
			
			
		}else{
			//This is an email so check for email in the database
			if(UserEndpoint.getUser(phoneNumber)!= null)
			{
				User user=UserEndpoint.getUser(phoneNumber);
				User user2=new User();
        		user2.setEmail(user.getEmail());
        		user2.setPhone_number(user.getPhone_number());
        	    	return user2;
			}
		}
		
		return null;
	}
	
	
	private String CreateFriendshipKey(String userEmail,String friendEmail)
	{
		
		return userEmail+"::"+friendEmail;
	}
		
}
