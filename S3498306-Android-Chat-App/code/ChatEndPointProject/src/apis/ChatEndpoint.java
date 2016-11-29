package apis;

import com.fahad.chatEngine.entity.Chat;
import com.fahad.chatEngine.entity.ChatRequestTemplate;
import com.fahad.chatEngine.entity.Friend;
import com.fahad.chatEngine.entity.TopMostCommonWords;
import com.fahad.chatEngine.entity.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

@Api(name = "chatendpoint", namespace = @ApiNamespace(ownerDomain = "fahad.com", ownerName = "fahad.com", packagePath = "chatEngine.entity"))
public class ChatEndpoint {
	
	//Get all the messages from the database
	@ApiMethod(name="testMessages")
	public CollectionResponse<Chat> testMessages(ChatRequestTemplate chatRequest)
	{
		

		//Validate that all fields exists 
				if(chatRequest.getFriendshipKey()==null||chatRequest.getToken()==null||chatRequest.getSenderEmail()==null)
				{	
					throw new EntityNotFoundException("missing field: "+chatRequest.getFriendshipKey()+" || "+chatRequest.getToken()+" || "+chatRequest.getSenderEmail());
				}
				//Validate the email and token for the user
				User user=UserEndpoint.getUser(chatRequest.getSenderEmail());
				if(user==null || user.getToken().equals(chatRequest.getToken())==false)
				{
					throw new EntityNotFoundException("token mismatch");
				}
				//validate that they are still friends by looking for the friendsKey
				Friend friend=new Friend();
				friend.setKey(chatRequest.getFriendshipKey().split("::")[0], chatRequest.getFriendshipKey().split("::")[1]);
				if(!FriendEndpoint.containsFriend(friend))
				{
					throw new EntityNotFoundException("friendship key doesn't exists");
				}
				//Now i need to look at the database and get all the messages between the two users which means i need
				//To look for the sender messages and the receiver messages , in another words 2 keys

				PersistenceManager mgr = null;
				List<Chat> execute = null;
				List<Chat> chats = new ArrayList<Chat>();
				try {
					//validate the user email and token
					mgr = getPersistenceManager();
					
					//end
					String friendshipKeyPar=chatRequest.getFriendshipKey();
					String friendshipKeyPar2=chatRequest.getFriendshipKey().split("::")[1]+"::"+chatRequest.getFriendshipKey().split("::")[0];
					
					//First query
					Query query = mgr.newQuery(Chat.class);
					query.setFilter("friendshipKey == friendshipKeyPar");
					query.declareParameters("String friendshipKeyPar");
					execute = (List<Chat>) query.execute(friendshipKeyPar);
					
					//Second
					Query query2 = mgr.newQuery(Chat.class);
					query2.setFilter("friendshipKey == friendshipKeyPar");
					query2.declareParameters("String friendshipKeyPar");
					List<Chat> execute2 = (List<Chat>) query2.execute(friendshipKeyPar2);


					if(execute == null && execute2 !=null && execute2.size()>0)
					{
						execute=execute2;
					}else if(execute2 != null && execute2.size()>0 && execute != null)
					{
						List<Chat> temp=new ArrayList<Chat>();
						//Join 2 with 1

						temp.addAll(execute);
						temp.addAll(execute2);
						execute=temp;
					}
					
					//Mark messages as read if the request came from the friend 
					//His messages will be read because i received them  but not mine		
					for(Chat tempChat:execute2)
					{
						tempChat.setIsRead(true);
						mgr.makePersistent(tempChat);
					}
					

				} finally {
					mgr.close();
				}
				if(execute==null || execute.size()<1)
				{
					throw new EntityNotFoundException("no messages found");
				}
				
	
				
				return CollectionResponse.<Chat> builder().setItems(execute).build();
	}

	@ApiMethod(name = "insertChat")
	public Chat insertChat(ChatRequestTemplate chat) 
	{
		PersistenceManager mgr = getPersistenceManager();
		
		//Validate that all fields exists 
		if(chat.getFriendshipKey()==null||chat.getMessage()==null||chat.getToken()==null||chat.getSenderEmail()==null)
		{
			throw new EntityNotFoundException("missing field");
		}
		//Validate the email and token for the user
		User user=UserEndpoint.getUser(chat.getSenderEmail());
		if(user==null || user.getToken().equals(chat.getToken())==false)
		{
			throw new EntityNotFoundException("token mismatch");
		}
		//validate that they are still friends by looking for the friendsKey
		Friend friend=new Friend();
		friend.setKey(chat.getFriendshipKey().split("::")[0], chat.getFriendshipKey().split("::")[1]);
		if(!FriendEndpoint.containsFriend(friend))
		{
			throw new EntityNotFoundException("friendship key doesn't exists");
		}
		//start inserting into the database
		Chat chat2=new Chat(chat.getMessage(),chat.getSenderEmail(),chat.getFriendshipKey());
		try {
			if (containsChat(chat2)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.makePersistent(chat2);
		}catch(Exception e)
		{
			throw new EntityExistsException(e.getMessage());
		}finally {
			mgr.close();
		}
		return chat2;
	}



	@ApiMethod(name = "removeChat")
	public void removeChat(@Named("id") String id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			Chat chat = mgr.getObjectById(Chat.class, id);
			mgr.deletePersistent(chat);
		} finally {
			mgr.close();
		}
	}

	private boolean containsChat(Chat chat) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Chat.class, chat.getKey());
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

	

	
	
	@ApiMethod(name = "topTenMostCommonWords")
	public CollectionResponse<TopMostCommonWords> topTenMostCommonWords()
	{
		PersistenceManager mgr = getPersistenceManager();
		List<Chat> execute = null;
		String messages="";
		ArrayList<TopMostCommonWords> top10=new ArrayList<TopMostCommonWords>();
		try {
			
			Query query = mgr.newQuery(Chat.class);
			execute = (List<Chat>) query.execute();
			//We add all the messages into one text
			for(Chat chat:execute)
			{
			messages=messages+" "+chat.getMessage();
			
			}
			//Clean the messages 
			messages=messages.replaceAll("[^a-zA-Z0-9_. ]+", "").trim();
			String[] data2=messages.split(" ");
			List<String> cleanWords=new ArrayList<String>();
			for(String i:data2)
			{
				if(i.toLowerCase().equals("a") || i.toLowerCase().equals("an") || i.length()<3)
					{
						continue;
					}
				switch(i.toLowerCase().trim())
				{
				case "are":break;
				case "where":break;
				case "who":break;
				case "when":break;
				case "what":break;
				case "you":break;
				case "they":break;
				case "how":break;
				case "that":break;
				case "and":break;
				case "can":break;
				case "will":break;
				case "from":break;
				case "the":break;
				case "also":break;
				case "which":break;
				case "should":break;
				case "may":break;
				case "for":break;
				
				default:	
					cleanWords.add(i.toLowerCase().trim());
				}	
			}
			HashMap<String,Integer> words=new HashMap<String,Integer>();
			//Counting how much a word occur and increasing the counter for that word
			for(String key:cleanWords)
			{
				//System.out.println(key);
				if(words.containsKey(key.toLowerCase().trim()))
				{
					words.put(key, words.get(key)+1);
				}else{
					words.put(key,1);
				}
			}
			
			//Finally we start sorting and get the sorted hashmap
			Map<String, Integer> sortedArray=sortByComparator(words,false);
			//We only need to take the first 10 so 
			
			if(sortedArray.size()>=10)
			{
				int count=0;
				
				for(String key:sortedArray.keySet())
				{
					if(count==10)
					{
						break;
					}
					top10.add(new TopMostCommonWords(key));
					count+=1;
				}
				
			}else{
				//get all
				for(String key:sortedArray.keySet())
				{
					top10.add(new TopMostCommonWords(key));
					
				}
			}
		} catch(Exception e)
		{
			throw new EntityExistsException(e.getMessage());
		}finally 
		{
			mgr.close();
		}
	 return	CollectionResponse.<TopMostCommonWords> builder().setItems(top10).build();
		
	}

	//http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
	 private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
	    {

	        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

	        // Sorting the list based on values
	        Collections.sort(list, new Comparator<Entry<String, Integer>>()
	        {
	            public int compare(Entry<String, Integer> o1,Entry<String, Integer> o2)
	            {
	                if (order)
	                {
	                    return o1.getValue().compareTo(o2.getValue());
	                }
	                else
	                {
	                    return o2.getValue().compareTo(o1.getValue());

	                }
	            }
	        });

	        // Maintaining insertion order with the help of LinkedList
	        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
	        for (Entry<String, Integer> entry : list)
	        {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }

	        return sortedMap;
	    }

	
	 
	 @ApiMethod(name = "getAllChats")
	 public CollectionResponse<Chat> getAllMessages(@Named("email") String email,@Named("token") String token)
	 {
try{
	if(email==null || token==null)
	{
		throw new EntityNotFoundException("user == null");
	}
	
	User user = null;
	try {
		user = UserEndpoint.getUser(email.trim().toLowerCase());
	} catch(Exception e)
	{
		throw new EntityNotFoundException("Here");
		
	}
	if(user==null)
	{
		throw new EntityNotFoundException("user2==null");
	}
	if(user.getToken().trim().equals(token.trim())==false)
	{
		throw new EntityNotFoundException("token mismatch");
	}
	

		PersistenceManager mgr = null;
	List<Chat> execute = null;
	  try {
		//validate the user email and token
		mgr = getPersistenceManager();
			//First query
		Query query = mgr.newQuery(Chat.class);
		execute = (List<Chat>) query.execute();
		

		if(execute == null || execute.size()<1)
		{
			throw new EntityNotFoundException("no messages found");
			
		} 
		
			List<Chat> temp=new ArrayList<Chat>();
			
			for(int i=0;i<execute.size();i++)
			{
				if(execute.get(i).getFriendshipKey().trim().toLowerCase().contains(user.getEmail().toLowerCase().trim()))
				{
					temp.add(new Chat(execute.get(i)));
				}	
			}
			execute=null;
			execute=temp;
		
		//Mark messages as read if the request came from the friend 
		//His messages will be read because i received them  but not mine		
		for(Chat tempChat:temp)
		{
			if(tempChat.getSenderEmail().trim().toLowerCase().equals(email.trim().toLowerCase())==false)
			{
				try {
					Chat chat=mgr.getObjectById(Chat.class, tempChat.getKey());
					chat.setIsRead(true);
				    mgr.makePersistent(chat);
				} catch (javax.jdo.JDOObjectNotFoundException ex) 
				{
					throw new EntityNotFoundException("Trying to change message to read: "+ex.getMessage());
				} 
	

			}
		}
		

	}catch(Exception e)
	{
		
		throw new EntityNotFoundException("First this: "+e.getMessage());
	}
	
	finally {
		mgr.close();
	}
	if(execute==null || execute.size()<1)
	{
		throw new EntityNotFoundException("no messages found");
	}
	
	return CollectionResponse.<Chat> builder().setItems(execute).build();

	}catch(Exception e)
	{
		throw new EntityNotFoundException("Check this: "+e.getMessage());
	}
		 
	 }
}
