package alkamli.fahad.chat.chat.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import alkamli.fahad.chat.chat.Chat.Chat_Template;
import alkamli.fahad.chat.chat.Classes.Friend;
import alkamli.fahad.chat.chat.Classes.Message;
import alkamli.fahad.chat.chat.R;
import alkamli.fahad.chat.chat.homePage.home;
import alkamli.fahad.chat.chat.model.ArrayList2;
import alkamli.fahad.chat.chat.model.CommonFunctions;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class FetchNewMessagesService extends IntentService {

    NotificationCompat.Builder notify;
    private static int uniqueID=1;
    private WindowManager windowManager;
    private TextView view;
    public static boolean Running=false;
    public  static String TAG="Alkamli";
     static final ArrayList2<Message> messages=new ArrayList2<Message>();

    public FetchNewMessagesService()
    {
        super("MyService");
    }

    int count=6;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message message) {

try {
    // This is where you do your work in the UI thread.
    // Your worker tells you in the message what to do.
    Log.d(TAG, "handleMessage");
    if (Running) {

        //make sure if the user is opening the chat windows not to notify him
        if (Chat_Template.to != null && Chat_Template.chatRunning) {
            //Check if this message key equals to the same message key //message key is the friendship key
            String friendshipKey = message.getData().getString("key");
            if (friendshipKey.trim().toLowerCase().contains(Chat_Template.to.trim().toLowerCase())) {
                //We already have the window open so no need to notify the user
                return;
            }
        }
        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());
        String title = null;
        String messageBody = message.getData().getString("messageBody");
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (message.getData().getString("friendEmail") != null && home.getFriendsList() != null && home.getFriendsList().size() > 0)
        {
            for (Friend friend : home.getFriendsList())
            {
                if (friend.getEmail().toLowerCase().trim().equals(message.getData().getString("friendEmail")))
                {
                    title=friend.getName();
                    if (friend.getProfileIcon() != null)
                    {
                        //http://stackoverflow.com/questions/23836920/how-to-set-bitmap-as-notification-icon-in-android
                        mBuilder.setLargeIcon(friend.getProfileIcon());
                        mBuilder.setSmallIcon(R.drawable.blank);
                    }else{
                        Log.d(TAG,"This friend doesn't have a profile image: "+friend.getEmail());
                        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                        //
                    }
                }
            }

        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(messageBody);
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(defaultRingtoneUri);
        Intent i=new Intent(getApplicationContext(),Chat_Template.class);
        i.putExtra("to",message.getData().getString("friendEmail"));
        i.putExtra("title",title);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, i, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        // mBuilder.setWhen(Long.parseLong());
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(message.getData().getString("key"), count, mBuilder.build());

    }
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"inside onStartCommand");

        if(Running==false)
        {
                Runnable run2=new Runnable()
                {
                    @Override
                    public void run() {
                        do {
                            try {
                                getMessages(getApplicationContext());
                                wait(10000);
                                Log.d(TAG,"Check again for new chats");
                            }catch(Exception e)
                            {
                                Log.d(TAG,e.getMessage());
                            }
                        }while(true);
                    }
                };

                new Thread(run2).start();
            this.Running=true;
            Log.d(TAG,"Service started");

        }
        return Service.START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }



    private boolean newMessage=false;

    private void getMessages(final Context activity)
    {

        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);
        String email=CommonFunctions.clean(sharedPref.getString("email",null));
        String token=CommonFunctions.clean(sharedPref.getString("token",null));
        String template="https://chat-api-s3498306.appspot.com/_ah/api/chatendpoint/v1/collectionresponse_chat/emailPar/tokenPar";
        template=template.replace("emailPar",email).replace("tokenPar",token);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        int count=0;
        OkHttpClient client=null;
        Response response=null;
        RequestBody body = RequestBody.create(JSON, template);
        Request request = new Request.Builder()
                .url(template)
                .build();
        client = new OkHttpClient();
        do {
            try {
                if(count==3)
                {
                    Toast.makeText(activity,"Error: please check your internet connection",Toast.LENGTH_LONG).show();

                    synchronized (this)
                    {
                        wait(10000);
                    }
                }
                //  Log.d(TAG,"Start");
                response = client.newCall(request).execute();
                //  Log.d(TAG,"end");
                if (response.code() == 200)
                {
                    // messages.clear();
                    String reply = response.body().string();
                    JSONObject obj = new JSONObject(reply);
                    //JSONObject obj2= obj.getJSONObject("items");
                    //Log.d(TAG,reply);
                    JSONArray array = obj.getJSONArray("items");

                    for (int i = 0; i < array.length(); i++)
                    {
                        JSONObject obj2 = array.getJSONObject(i);
                        if(obj2.length()<1)
                        {
                            break;
                        }
                        try{
                            String temp=   obj2.getString("senderEmail");
                        }catch(Exception e)
                        {
                            // Log.d(TAG,"This exception ");
                            //Log.d(TAG,e.getMessage());
                            continue;
                        }
                        //boolean read=obj2.getBoolean("isRead");
                        boolean read=obj2.getBoolean("isRead");
                        if(obj2.getString("senderEmail").trim().toLowerCase().equals(email.trim().toLowerCase()))
                        {
                            //This is my message
                           // Log.d(TAG,"This is my message");
                            boolean me=true;
                            insertMessageToArrayList(new Message(obj2.getString("message"), obj2.getString("sendDateTime"),me,read, obj2.getString("friendshipKey")));
                        }else{
                           // Log.d(TAG,"This is the other guy's message");
                           // Log.d(TAG,reply);
                            boolean me=false;
                            insertMessageToArrayList(new Message(obj2.getString("message"), obj2.getString("sendDateTime"),me,read, obj2.getString("friendshipKey")));
                        }
                    }
                    //before you send the array sort it by date first
                   // Collections.sort(messages);
                    newMessage=false;
                }else{
                    String reply=response.body().string();
                    //Log.d(TAG,reply);
                    if(reply.contains("no messages found"))
                    {
                       // Log.d(TAG,"No messages found");
                        //break;
                        synchronized (this)
                        {
                            response.body().close();
                           // wait(7000);
                            wait(70000);
                          //  Log.d(TAG,"Waiting before sending another get message request");
                        }

                    }
                }

                synchronized (this)
                {
                    response.body().close();
                    wait(700);
                    //  Log.d(TAG,"Waiting before sending another get message request");
                }


            } catch (Exception e) {

                Log.d(TAG,e.getMessage());
                count++;
            }
        }while(true);

    }












    // i am adding messages one by one therefor if the message already exists no need to check the in the rest of the array
    private synchronized void insertMessageToArrayList(Message message)
    {
        try {
            for (Message m : messages)
            {
                if (m.getMessage().trim().equals(message.getMessage().trim()) && m.getDate().equals(message.getDate()))
                {
                    //Log.d("Alkamli","Message is already in the list");
                   return;
                }
              //  Log.d(TAG,m.getDate());
            }
                Log.d("Alkamli", "Message added");
                messages.add(message);
                //Collections.sort(messages);
                if (message.isRead()==false && message.isMe()==false)
                {
                    //we have at least one new message
                    newMessage=true;
                    {
                        Log.d(TAG,"Please notify the user of a new message");
                        //send notification
                        android.os.Message messageTemp=new android.os.Message();
                        Bundle bundle=new Bundle();
                        bundle.putString("title","New Message");
                        bundle.putString("key",message.getFrendship_key());
                        bundle.putString("messageBody",message.getMessage());
                        String friendEmail=null;
                        if(message.isMe())
                        {
                            friendEmail=message.getFrendship_key().split("::")[1];
                        }else{
                            friendEmail=message.getFrendship_key().split("::")[0];
                        }
                        bundle.putString("friendEmail",friendEmail);
                        messageTemp.setData(bundle);
                        mHandler.sendMessage(messageTemp);
                        //return;
                        //wait a sec
                        synchronized (this)
                        {
                            try{
                                wait(1000);
                            }catch(Exception e)
                            {

                            }
                        }
                    }
                }

        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());
        }
    }


    public static synchronized ArrayList<Message> getAllMessages(String to)
    {
        ArrayList<Message> temp=new ArrayList<Message>();

       // Log.d(TAG,"Testing getAllMessages");
        if(messages.size()>0)
        {

            for(Message tempMessage:messages)
            {
                if(tempMessage.getFrendship_key().toLowerCase().trim().contains(to.toLowerCase().trim()))
                {

                    if(tempMessage.getMessage().equals("fahad 1"))
                    {
                        Log.d(TAG,tempMessage.getDate());
                    }
                    //add the message to the array
                    temp.add(tempMessage);
                }
            }
            Collections.sort(temp);

            return temp;

        }


        return null;
    }
}
