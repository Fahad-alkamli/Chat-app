package apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.fahad.chatEngine.entity.AdminRequest;
import com.fahad.chatEngine.entity.Chat;
import com.fahad.chatEngine.entity.Friend;
import com.fahad.chatEngine.entity.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

@Api(name = "userendpoint", namespace = @ApiNamespace(ownerDomain = "example.com", ownerName = "example.com", packagePath = "myproject"))
public class UserEndpoint {


	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listUser")
	public CollectionResponse<User> listUser() {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<User> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(User.class);
			execute = (List<User>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (User obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<User> builder().setItems(execute).build();
	}


	@ApiMethod(name = "getUser")
	public User getUser(@Named("email") String email, @Named("password") String password) {
		PersistenceManager mgr = getPersistenceManager();
		User user = null;
		try {
			user = mgr.getObjectById(User.class, email);
			
			if(user.getPassword().equals(password)==false)
			{
				throw new EntityNotFoundException("wrong password");
			}
			if(user != null)
			{
			//Update the user token after he logs in
			user.setToken(MD5());
			mgr.makePersistent(user);
			user = mgr.getObjectById(User.class, email);
			}
		} finally {
			mgr.close();
		}
		return user;
	}


	@ApiMethod(name = "insertUser")
	public User insertUser(User user) {
		PersistenceManager mgr = getPersistenceManager();
		try {

			if (containsUser(user)) {
				throw new EntityExistsException("This user already registered");
			}
			if(user.getEmail() == null || user.getPassword() == null || user.getPhone_number() ==null)
			{		
				throw new EntityExistsException("missing required fields");
			}
			if(FriendEndpoint.friendExists(user.getPhone_number(),user.getEmail())!=null)
			{
				//This phone number is registered
				throw new EntityExistsException("This phone number is registered");
				
			}
			user.setToken(MD5());
			if(user.getDisplayName() == null)
			{
				user.setDisplayName("No name");
			}
			user.setEmail(user.getEmail().toLowerCase().trim());
			mgr.makePersistent(user);
		} finally {
			mgr.close();
		}
		return user;
	}

	
	
	protected static User getUser(@Named("email") String email)
	{
		PersistenceManager mgr = getPersistenceManager();
		User user = null;
		try {
			user = mgr.getObjectById(User.class, email.toLowerCase().trim());
		} catch(Exception e)
		{
			
		}finally 
		{
			mgr.close();
		}
		return user;
	}

	//The user can't change his/her Email need to fix that one later
	@ApiMethod(name = "updateUser")
	public User updateUser(User user) {
		PersistenceManager mgr = getPersistenceManager();
		User user2;
		try {
			if (!containsUser(user)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			if(user.getToken() ==null)
			{
				throw new EntityNotFoundException("Process needs a token.");
			}
			 user2=getUser(user.getEmail());
			 
			 //Validate the tokens to make sure the user has permission to update his/her profile
			 if(!user.getToken().trim().equals(user2.getToken()))
			 {
				 throw new EntityNotFoundException("token mismatch "); 
			 }
			
			if(user.getPassword() != null)
			{
				user2.setPassword(user.getPassword());
			}
			if(user.getPhone_number() != null)
			{
				user2.setPhone_number(user.getPhone_number());
			}
			if(user.getDisplayName() != null)
			{
				user2.setDisplayName(user.getDisplayName());
			}
			//Here
			if(user.getProfilePicture() != null)
			{
				user2.setProfilePicture(user.getProfilePicture());
			}
			mgr.makePersistent(user2);
		} finally {
			mgr.close();
		}
		return user2;
	}

	@ApiMethod(name = "removeUser")
	public void removeUser(@Named("email") String email) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			User user = mgr.getObjectById(User.class, email.toLowerCase().trim());
			mgr.deletePersistent(user);
		} finally {
			mgr.close();
		}
	}

	private boolean containsUser(User user) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(User.class, user.getEmail().toLowerCase().trim());
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			contains = false;
		} finally {
			mgr.close();
		}
		return contains;
	}

	public static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

	//http://stackoverflow.com/questions/415953/how-can-i-generate-an-md5-hash
	 private String MD5() {
		   try {
			   String md5 = Long.toString(System.currentTimeMillis());
			   md5+="Alkamli";
			   Random rnd=new Random();
			   md5+=Integer.toString(rnd.nextInt(999999));
			   md5+=Integer.toString(rnd.nextInt(999999));
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(md5.getBytes());
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) 
		        {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    }
		    return null;
		}
	
	 @ApiMethod(name="GetTotalUsers")
	 public AdminRequest GetTotalUsers(User user)
	 {
			if(!containsUser(user)){
				throw new EntityExistsException("User doesn't exists");
			}
			if(!validateUserAndToken(user.getEmail(),user.getToken()))
			{
				throw new EntityExistsException("invalid user or token");
			}
			
			User user2=getUser(user.getEmail());
			if(user2.isAdmin()==false)
			{
				throw new EntityExistsException("User is not an admin");
			}
			//Process the request here 
			
			CollectionResponse<User>  list=listUser();
			
			AdminRequest request=new AdminRequest();
			request.setValue(Integer.toString(list.getItems().size()));
			return request;
		 
	 }
	 
	 @ApiMethod(name="GetTotalChatHistory")
	 public AdminRequest GetTotalChatHistory(User user)
	 {
			if(!containsUser(user)){
				throw new EntityExistsException("User doesn't exists");
			}
			if(!validateUserAndToken(user.getEmail(),user.getToken()))
			{
				throw new EntityExistsException("invalid user or token");
			}
			
			User user2=getUser(user.getEmail());
			if(user2.isAdmin()==false)
			{
				throw new EntityExistsException("User is not an admin");
			}
			//Process the request here 
			
			PersistenceManager mgr = null;
			List<Chat> execute = null;

			try {
				mgr = getPersistenceManager();
				Query query = mgr.newQuery(Chat.class);
				execute = (List<Chat>) query.execute();
			} finally {
				mgr.close();
			}
			if(execute==null)
			{
				throw new EntityExistsException("Error: Can't execute this command");
				
			}
			AdminRequest request=new AdminRequest();
			request.setValue(Integer.toString(execute.size()));
			return request;
	 }
	 
	 //AverageUnreadMessages unreadMessages/TotalMessages
	 @ApiMethod(name="AverageUnreadMessages")
	 public AdminRequest AverageUnreadMessages(User user)
	 {
			if(!containsUser(user)){
				throw new EntityExistsException("User doesn't exists");
			}
			if(!validateUserAndToken(user.getEmail(),user.getToken()))
			{
				throw new EntityExistsException("invalid user or token");
			}
			
			User user2=getUser(user.getEmail());
			if(user2.isAdmin()==false)
			{
				throw new EntityExistsException("User is not an admin");
			}
			//Process the request here 
			List<Chat> execute = null;
			List<Chat> execute2 = null;
	
			try {
				//validate the user email and token
			PersistenceManager	mgr = getPersistenceManager();
				
				Query query = mgr.newQuery(Chat.class);
				query.setFilter("IsRead == true");	
				execute = (List<Chat>) query.execute();
				query = mgr.newQuery(Chat.class);
				query.setFilter("IsRead == false");	
				execute2 = (List<Chat>) query.execute();
				
				
			}catch(Exception e)
			{
				
			}
			
			if(execute2==null)
			{
				//throw new EntityExistsException("Error: Can't process AverageUnreadMessages command.");
				execute2= new ArrayList<Chat>();
			}else if(execute==null)
			{
				execute= new ArrayList<Chat>();
			}
			
			
			AdminRequest request=new AdminRequest();
			request.setValue(Integer.toString(execute2.size())+"/"+Integer.toString(execute.size()));
			return request;
	 }

	@ApiMethod(name = "refreshToken")
	public User refreshToken(User user) 
		{
			PersistenceManager mgr = getPersistenceManager();
			User user2 = null;
			try {
				user2 = mgr.getObjectById(User.class, user.getEmail());
				
				if(user2 == null)
				{
					throw new EntityNotFoundException("User doesn't exists");
				}
				if(user2.getPassword().equals(user.getPassword())==false)
				{
					
					throw new EntityNotFoundException("wrong password");
				}

			} finally {
				mgr.close();
			}
			return user2;
	}


	@ApiMethod(name = "makeAdmin")
	public void makeAdmin(User user,@Named("validationWord") String validationWord)
	{
		if(!validationWord.trim().toLowerCase().equals("alkamli"))
		{
			return;
		}
		if(!containsUser(user)){
			return;
		}
		if(!validateUserAndToken(user.getEmail(),user.getToken()))
		{
			return;
		}
		
		PersistenceManager mgr=getPersistenceManager();
		User user2=getUser(user.getEmail());
		user2.setAdmin(true);
		mgr.makePersistent(user2);	
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


}
