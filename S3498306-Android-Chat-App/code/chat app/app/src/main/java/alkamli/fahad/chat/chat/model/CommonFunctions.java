package alkamli.fahad.chat.chat.model;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import alkamli.fahad.chat.chat.Classes.Friend;
import alkamli.fahad.chat.chat.R;
import alkamli.fahad.chat.chat.Register_Activity;
import alkamli.fahad.chat.chat.homePage.fragments.Friends;
import alkamli.fahad.chat.chat.homePage.home;
import alkamli.fahad.chat.chat.pickPhoneContact_Activity;
import alkamli.fahad.chat.chat.welcomePage_Activity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class CommonFunctions {

  public  enum errors
    {
        MissingDot,MissingAt,MissingCountryCode,ShortLength,Good
    }
    static OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

   static String TAG="Alkamli";
    public static String registerTemplate ="{'email':'emailHere','password' : 'passwordHere','phone_number' : 'phone_numberHere'}";

    public static String addFriendTemplate="{'user_Email':'user_EmailHere','friend_Email':'friend_EmailHere'}";
    public static String loginUrl="https://chat-api-s3498306.appspot.com/_ah/api/userendpoint/v1/user/email/password";
    public static errors isEmailValid(String email) {
        if(!email.contains("@"))
        {
            return errors.MissingAt;
        }
        if(!email.contains("."))
        {
            return errors.MissingDot;
        }

            return errors.Good;
    }

    public static errors isPasswordValid(String password) {
        if(password.length() < 1)
        {
            return errors.ShortLength;
        }

        return errors.Good;
    }

    public static errors isPhoneNumberValid(String phoneNumber)
    {
 if(phoneNumber.contains("+")==false)
        {
            return errors.MissingCountryCode;
        }  else if( phoneNumber.length() < 12)
    {
        return errors.ShortLength;
    }

        return errors.Good;
    }

  public static  String getErrorMessage(String response)
    {
        try {
            JSONObject obj = new JSONObject(response);
            JSONObject temp = obj.getJSONObject("error");
            JSONArray temp2 = temp.getJSONArray("errors");
            JSONObject finallyHere = temp2.getJSONObject(0);
            //Log.d(TAG, finallyHere.getString("message"));
            return  finallyHere.getString("message");
        }catch(Exception e)
        {
            Log.d("Alkamli",e.getMessage());
        }
        return null;
    }

  public static boolean Register(String email,String password,String phone)
    {
        try {

            String login=CommonFunctions.registerTemplate.replace("emailHere",email).replace("passwordHere",password);
            login=login.replace("phone_numberHere",phone);
            Log.d(TAG,login);
            RequestBody body = RequestBody.create(JSON, login);
            Request request = new Request.Builder()
                    .url("https://chat-api-s3498306.appspot.com/_ah/api/userendpoint/v1/user")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            //Handle an Exception
            if(response.code() != 200)
            {
                Log.d(TAG,"Check this: Code :"+Integer.toString(response.code()));
                String Response=response.body().string();
                String Message= CommonFunctions.getErrorMessage(Response);
                if(Message != null && Message.contains("Object already exists"))
                {
                    Log.d(TAG,"This email is already registered.");
                    sendToast(Register_Activity.activity,"Error: This email is already registered.");
                }else
                {
                    Log.d(TAG,Response);
                }

            }else
            {
                String reply=response.body().string();
                Log.d(TAG,reply);
                Log.d(TAG,Integer.toString(response.code()));
                return true;
            }

        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }
        return false;
    }

   public static boolean Login(Activity activity,String email, String password)
    {
        try {
            String login=loginUrl.replace("email",email.trim()).replace("password",password.trim());
            Log.d(TAG,login);
            Request request = new Request.Builder()
                    .url(login)
                    .build();
            client = new OkHttpClient();
            Response response = client.newCall(request).execute();

            //return response.body().string();
           // String reply=response.body().string();
            JSONObject obj=new JSONObject(response.body().string());
           String token= obj.getString("token");
            String phone_number=obj.getString("phone_number");
            String profilePicture=null;
            String displayName=null;
            boolean admin=false;
            //These will not always be there !
            try {
                profilePicture = obj.getString("profilePicture");
            }catch(Exception e)
            {
                class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());



            }
            try {
                displayName = obj.getString("displayName");
                admin=obj.getBoolean("admin");
                Log.d(TAG,Boolean.toString(admin));
            }catch(Exception e)
            {
                class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

            }
            if(token != null)
            {

                Log.d(TAG, token);
                SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("email", clean(email));
                editor.putString("password", password);
                editor.putString("token", clean(token));
                editor.putString("phone_number",clean(phone_number));
                if(displayName !=null)
                {editor.putString("displayName", displayName);}
                if(profilePicture!= null)
                {editor.putString("profilePicture",clean(profilePicture));}
                if(admin !=false)
                {editor.putBoolean("admin", admin);}


                editor.commit();

                Log.d(TAG,"loggin successfully");
                return true;
            }
        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }
        //https://chat-api-s3498306.appspot.com/_ah/api/userendpoint/v1/user/d0l1%40hotmail.com/alkamli
        return false;
    }


    public static String clean(String value)
    {
        try{

          return  value.trim().replace(" ","");
        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }
        return null;
    }




    public static void logout(Activity activity)
    {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
        Intent i = new Intent(activity, welcomePage_Activity.class);
        activity.startActivity(i);
        activity.finish();
    }

    public static boolean alreadyLoggedIn(final Activity activity)
    {
        //https://developer.android.com/training/basics/data-storage/shared-preferences.html
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);
        String token= sharedPref.getString("token",null);
         String email=sharedPref.getString("email",null);
         String password=sharedPref.getString("password",null);

        if(email != null && token != null && password != null)
        {
            return true;
        }
        return false;
    }


    public static Bitmap getImage(String Picurl)
    {
    try {
        //http://stackoverflow.com/questions/5776851/load-image-from-url
            URL url = new URL(Picurl);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
           // imageView.setImageBitmap(bmp);
        return bmp;
        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }

        return null;

    }


    public static void sendToast(final Activity activity,final String message)
    {

        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity,message,Toast.LENGTH_LONG).show();

            }
        });
    }


    public static ArrayList<String> doesFriendExists( ArrayList<String> numbers,Activity activity)
    {
        ArrayList<String> foundNumbers = new ArrayList<String>();
        try {

            if(activity==null)
            {
                Log.d(TAG,"activity==null");
            }

            if (numbers ==null || numbers.size() < 1 )
            {
                Log.d(TAG,"size==0");
                return null;
            }
            String login = "https://chat-api-s3498306.appspot.com/_ah/api/friendendpoint/v1/friendExists/phone/email/token";
            Log.d(TAG,"check1");
            SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);
            Log.d(TAG,"check2");

            //build the post

            String template = "{'phoneNumbers': numbers ,'token':'tokenHere', 'userEmail':'emailHere'}";
            String temp = "[";
            boolean first = false;
            for (String num : numbers) {
                if (first == false) {
                    temp += "'" + num + "'";
                } else {
                    temp += ", '" + num + "'";
                }

                first = true;
            }
            temp += "]";
            Log.d(TAG,temp);
            template = template.replace("numbers", temp);

            template= template.replace("tokenHere", sharedPref.getString("token", null)).replace("emailHere", sharedPref.getString("email", null));

            //https://chat-api-s3498306.appspot.com/_ah/api/friendendpoint/v1/friendsExists
            RequestBody body = RequestBody.create(JSON, template);
            Request request = new Request.Builder()
                    .url("https://chat-api-s3498306.appspot.com/_ah/api/friendendpoint/v1/friendsExists")
                    .post(body)
                    .build();
            Log.d(TAG,template);
            OkHttpClient client = new OkHttpClient();
            Response response;
            int count = 0;
            do {
                try {
                    if (count == 3)
                    {
                        Log.d(TAG,"Give up");
                        return null;
                    }
                    response = client.newCall(request).execute();
                    break;
                } catch (Exception e) {
                   // Log.d(TAG, e.getMessage());
                    class Local {};  Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" "+e.getMessage());
                }
                count++;
            } while (true);
            if (response.code() == 200)
            {
                Log.d(TAG,"doesFriendExists");
                //read the object and add each number the server found
                //foundNumbers
                String reply = response.body().string();
                Log.d(TAG,reply);
                JSONObject obj = new JSONObject(reply);
                //JSONObject obj2= obj.getJSONObject("items");

                JSONArray array = obj.getJSONArray("items");
                for (int i = 0; i < array.length(); i++)
                {
                    JSONObject obj2 = array.getJSONObject(i);
                   // obj2.getString("phone_number");
                   if(!exists(foundNumbers,obj2.getString("phone_number")))
                   {
                       foundNumbers.add(obj2.getString("phone_number"));
                       Log.d(TAG, "Check me out");
                       //Log.d(TAG, obj2.getString("email"));
                      // Log.d(TAG, obj2.getString("phone_number"));
                   }
                }

                Log.d(TAG,"Stage 1");
            }else{
                Log.d(TAG,"check this");
            }
        }catch(Exception en)
        {

            class Local {};  Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" "+en.getMessage());
        }
        if(foundNumbers.size()>0)
        {
            return foundNumbers;
        }
     return null;
    }

    private static boolean exists(ArrayList<String> list, String value)
    {
            boolean yes=false;
            for(String temp:list)
            {

                if(temp.trim().toLowerCase().equals(value.trim().toLowerCase()))
                {
                    yes=true;
                    return yes;
                }
            }
            return yes;

        }

    //You need to send it to the server either Email or phone number
    public static boolean addFriends(ArrayList<String> friends, Activity activity)
    {
        String requestUrl="https://chat-api-s3498306.appspot.com/_ah/api/friendendpoint/v1/friend/token";

        //Log.d(TAG,"Check me 1");
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);
        //Log.d(TAG,"Check me 2");
        //Add each friend
        for(String friend:friends)
        {
            try {
                //build the request
                String token=sharedPref.getString("token",null);
                String email=sharedPref.getString("email",null);
                requestUrl=requestUrl.replace("token",token);
                String friendTemplate=addFriendTemplate.replace("user_EmailHere",email).replace("friend_EmailHere",friend);
                Log.d(TAG,friendTemplate);
                Log.d(TAG,requestUrl);
                RequestBody body = RequestBody.create(JSON,friendTemplate);
                Request request = new Request.Builder()
                        .url(requestUrl)
                        .post(body)
                        .build();
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if(response.code() ==200)
                {
                    Log.d(TAG,"Friend has been added");
                    Log.d(TAG,response.body().string());
                    //Request a friend list update?
                }else{
                    Log.d(TAG,response.body().string());
                    return false;
                }
            }catch(Exception e)
            {
                class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Friend> getFriendsList(Activity activity)
    {
        ArrayList<Friend> friendsList=null;
        int count=0;
        do {
            try {
                if(count>=3)
                {
                    break;
                }

                count++;
                //https://chat-api-s3498306.appspot.com/_ah/api/friendendpoint/v1/collectionresponse_user/d0l1%40hotmail.com/ea7ab62d7c9d279faac1aa4c2de93c7d
                String template = "https://chat-api-s3498306.appspot.com/_ah/api/friendendpoint/v1/collectionresponse_user/email/token";
                SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);
               String email=clean(sharedPref.getString("email", null));
                String token=clean(sharedPref.getString("token", null));

                template = template.replace("email",email ).replace("token",token);

               // Log.d(TAG, template);
                Request request = new Request.Builder()
                        .url(template)
                        .build();
                client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                if (response.code() == 200)
                {
                    count=3;
                    friendsList=new ArrayList<Friend>();
                    String reply = response.body().string();
                    JSONObject obj = new JSONObject(reply);
                    //JSONObject obj2= obj.getJSONObject("items");

                    JSONArray array = obj.getJSONArray("items");

                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject obj2 = array.getJSONObject(i);

                        String profilePicture = null;
                        try {
                            profilePicture = obj2.getString("profilePicture");
                        } catch (Exception e) {
                            class Local {
                            }
                            ;
                            //Log.d("Alkamli", "Sub: " + Local.class.getEnclosingMethod().getName() + " Error code: " + e.getMessage());
                        }

                        Friend friend = new Friend(obj2.getString("displayName"), obj2.getString("email"), null);
                        if (profilePicture != null) {
                           // Log.d(TAG, profilePicture);
                            friend.setPicture(profilePicture);
                        }
                        friendsList.add(friend);
                    }
                    //Log.d(TAG,reply);
                } else {
                    Log.d(TAG, "Error");
                    Log.d(TAG, response.body().string());
                    break;
                }
                break;
            } catch (Exception e) {
                class Local {
                }
                ;
                Log.d("Alkamli", "Sub: " + Local.class.getEnclosingMethod().getName() + " Error code: " + e.getMessage());
            }
        }while(true);

        return friendsList;
    }


    public static String getFriendKey(Context activity,String to)
    {
        if(to == null || to.trim().replace(" ","").length()<1)
        {
            return null;

        }

        //"::"
        SharedPreferences sharedPref =activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);

        String key=sharedPref.getString("email",null);
        if(key != null)
        {
            key+="::"+to.trim().replace(" ","");
            return key;
        }
        return null;
    }




    public static void deleteFriend(Activity activity, String friend_email)
    {
        String template="https://chat-api-s3498306.appspot.com/_ah/api/friendendpoint/v1/friend/email/friendPar/token";


        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);
        String email=clean(sharedPref.getString("email", null));
        String token=clean(sharedPref.getString("token", null));

        template = template.replace("email",clean(email.trim()) ).replace("token",clean(token)).replace("friendPar",clean(friend_email));

        Log.d(TAG, template);
        Request request = new Request.Builder()
                .url(template)
                .delete()
                .build();
        client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            if(response.code()==204)
            {
                sendToast(activity,"Friend has been deleted");
                Friends.removeFriend(clean(friend_email));

            }else{
                Log.d(TAG,response.body().string());
            }
        }catch(Exception e)
        {
            sendToast(activity,"Error: Couldn't delete a friend");
            class Local {
            }
            ;
            Log.d("Alkamli", "Sub: " + Local.class.getEnclosingMethod().getName() + " Error code: " + e.getMessage());
        }

    }



}
