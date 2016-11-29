package alkamli.fahad.chat.chat.Chat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import alkamli.fahad.chat.chat.Classes.Message;
import alkamli.fahad.chat.chat.R;
import alkamli.fahad.chat.chat.Services.FetchNewMessagesService;
import alkamli.fahad.chat.chat.model.ArrayList2;
import alkamli.fahad.chat.chat.model.CommonFunctions;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chat_Template extends AppCompatActivity {

    Activity activity;
    public static String to=null;
    String title;
    String TAG="Alkamli";


    public static boolean chatRunning=false;

    ArrayList<Message> tempMessages=new ArrayList<Message>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat__template);
        Log.d(TAG,"onCreate");
        to=getIntent().getExtras().getString("to");
        title=getIntent().getExtras().getString("title");
        TextView titleTextview=(TextView) findViewById(R.id.friendTitle);
        titleTextview.setText(title);
      Log.d(TAG,Integer.toString(tempMessages.size()));
        activity=this;
        chatRunning=true;


        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (this)
                {
                    do {
                        try {
                            getNewMessages();
                            wait(600);
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }

                    }while(chatRunning);

                }

            }
        }).start();
    }




    public void sendMessage(View view)
    {
        EditText messageEditView=(EditText) findViewById(R.id.sendMessageEditView);

        final String message=messageEditView.getText().toString().trim();
        messageEditView.setText("");
        if(message.replace(" ","").length()<1)
        {
            return;
        }
        Runnable run=new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    Looper.prepare();
                //Send the message
                String template = "{'friendshipKey': 'friendshipPar','message': 'messagePar','senderEmail': 'emailPar','token': 'tokePar'}";
                String friendKey = CommonFunctions.getFriendKey(activity, to);
                SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);
                template = template.replace("friendshipPar", friendKey).replace("messagePar", message);
                template = template.replace("emailPar", sharedPref.getString("email", null))
                        .replace("tokePar", sharedPref.getString("token", null));
                Log.d(TAG, template);

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, template);
                Request request = new Request.Builder()
                        .url("https://chat-api-s3498306.appspot.com/_ah/api/chatendpoint/v1/chat")
                        .post(body)
                        .build();
                OkHttpClient client = new OkHttpClient();
                Response response = null;
                int count = 0;
                do {
                    if (count == 6)
                    {
                        Toast.makeText(activity, "Error: please check your internet connection", Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        response = client.newCall(request).execute();
                        if (response.code() == 200) {
                            Log.d(TAG, "Message has been sent");
                            //getMessages(activity,to);
                            //Add the message to the list
                            break;
                        }
                    } catch (Exception e)
                    {
                        class Local {};Log.d(TAG, "Sub: " + Local.class.getEnclosingMethod().getName() + " Error code: " + e.getMessage());
                    }
                    count++;
                } while (true);
                //Call getMessages

            }catch(Exception e)
            {
                class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());
            }


            }
        };

        new Thread(run).start();

    }

    private void getNewMessages()
    {
        ArrayList<Message> temp=FetchNewMessagesService.getAllMessages(to);
       if(temp != null && temp.size()!=tempMessages.size() )
       {

           tempMessages=FetchNewMessagesService.getAllMessages(to);
           Collections.sort(tempMessages);
           //Do a refresh to the list
           activity.runOnUiThread(new Runnable()
           {
               @Override
               public void run() {
                   ListView messagesList = (ListView) findViewById(R.id.messagesListView);
                   messagesList.setAdapter(new ChatAdapter(activity, tempMessages));
                   messagesList.setSelection(tempMessages.size() - 1);
               }
           });
       }

    }

    @Override
    protected void onPause() {
        super.onPause();
        chatRunning=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatRunning=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatRunning=false;
    }
}
