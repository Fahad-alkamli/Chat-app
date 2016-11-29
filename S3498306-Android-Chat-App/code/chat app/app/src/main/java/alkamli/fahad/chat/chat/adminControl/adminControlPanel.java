package alkamli.fahad.chat.chat.adminControl;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import alkamli.fahad.chat.chat.R;
import alkamli.fahad.chat.chat.model.CommonFunctions;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class adminControlPanel extends AppCompatActivity {

    Activity activity;
    TextView totalUsersTextView=null;
    TextView GetTotalChatHistoryTextView=null;
    TextView AverageUnreadMessagesTextView=null;
    ListView topWordsListView=null;
    final String TAG="Alkamli";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_control_panel);
        activity=this;
        totalUsersTextView=(TextView) findViewById(R.id.totalUsersTextView);
        GetTotalChatHistoryTextView=(TextView) findViewById(R.id.GetTotalChatHistoryTextView);
        AverageUnreadMessagesTextView=(TextView) findViewById(R.id.AverageUnreadMessagesTextView);

        topWordsListView=(ListView) findViewById(R.id.topWordsListView);

        ArrayList<String> test=new ArrayList<String>();
        test.add("Test1");
        test.add("Test2");
        test.add("Test3");
        test.add("Test4");
        test.add("Test5");
        test.add("Test6");
        test.add("Test7");
        test.add("Test8");
        test.add("Test9");
        test.add("Test10");
        WordsListAdapter wordsAdapter=new WordsListAdapter(this,test);

        topWordsListView.setAdapter(wordsAdapter);

        Runnable run=new Runnable()
        {
            @Override
            public void run() {
                getTop10Words(topWordsListView);
            }
        };
        new Thread(run).start();
    }

     public void GetTotalUsers(final View view)
    {
        view.setEnabled(false);

        Runnable run=new Runnable()
        {
            @Override
            public void run()
            {

                SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);
                String email= CommonFunctions.clean(sharedPref.getString("email",null));
                String token=CommonFunctions.clean(sharedPref.getString("token",null));
                OkHttpClient client=null;
                Response response=null;
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                String template="{'email': 'emailPar','token': 'tokenPar'}";
                template=template.replace("emailPar",email).replace("tokenPar",token);
                RequestBody body = RequestBody.create(JSON, template);
                Request request = new Request.Builder()
                        .url("https://chat-api-s3498306.appspot.com/_ah/api/userendpoint/v1/GetTotalUsers")
                        .post(body)
                        .build();
                client = new OkHttpClient();
                try{

                    response = client.newCall(request).execute();
                    if(response.code()==200)
                    {
                        JSONObject obj = new JSONObject(response.body().string());
                      final  String value=obj.getString("value");

                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                totalUsersTextView.setText(value);
                            }
                        });
                    }
                }catch(Exception e)
                {
                    Log.d("Alkamli",e.getMessage());
                }
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                });

            }
        };

        new Thread(run).start();


    }

   public   void GetTotalChatHistory(final View view)
    {
        view.setEnabled(false);

        Runnable run=new Runnable()
        {
            @Override
            public void run()
            {

                SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);
                String email= CommonFunctions.clean(sharedPref.getString("email",null));
                String token=CommonFunctions.clean(sharedPref.getString("token",null));
                OkHttpClient client=null;
                Response response=null;
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                String template="{'email': 'emailPar','token': 'tokenPar'}";
                template=template.replace("emailPar",email).replace("tokenPar",token);
                RequestBody body = RequestBody.create(JSON, template);
                Request request = new Request.Builder()
                        .url("https://chat-api-s3498306.appspot.com/_ah/api/userendpoint/v1/GetTotalChatHistory")
                        .post(body)
                        .build();
                client = new OkHttpClient();
                try{

                    response = client.newCall(request).execute();
                    if(response.code()==200)
                    {
                        JSONObject obj = new JSONObject(response.body().string());
                        final  String value=obj.getString("value");

                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                GetTotalChatHistoryTextView.setText(value);
                            }
                        });
                    }
                }catch(Exception e)
                {
                    Log.d("Alkamli",e.getMessage());
                }
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                });

            }
        };

        new Thread(run).start();
    }
 public     void AverageUnreadMessages(final View view)
    {
        view.setEnabled(false);

        Runnable run=new Runnable()
        {
            @Override
            public void run()
            {

                SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);
                String email= CommonFunctions.clean(sharedPref.getString("email",null));
                String token=CommonFunctions.clean(sharedPref.getString("token",null));
                OkHttpClient client=null;
                Response response=null;
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                String template="{'email': 'emailPar','token': 'tokenPar'}";
                template=template.replace("emailPar",email).replace("tokenPar",token);
                RequestBody body = RequestBody.create(JSON, template);
                Request request = new Request.Builder()
                        .url("https://chat-api-s3498306.appspot.com/_ah/api/userendpoint/v1/AverageUnreadMessages")
                        .post(body)
                        .build();
                client = new OkHttpClient();
                try{

                    response = client.newCall(request).execute();
                    if(response.code()==200)
                    {
                        JSONObject obj = new JSONObject(response.body().string());
                        final  String value=obj.getString("value");

                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                AverageUnreadMessagesTextView.setText(value);
                            }
                        });
                    }
                }catch(Exception e)
                {
                    Log.d("Alkamli",e.getMessage());
                }
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                });

            }
        };

        new Thread(run).start();

    }

   public  void getTop10Words(final ListView topWordsListView)
    {

        try {
            URL url = new URL("https://chat-api-s3498306.appspot.com/_ah/api/chatendpoint/v1/topTenMostCommonWords");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

            byte[] post="{}".getBytes();
            out.write(post);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            String response="";
            while((line=reader.readLine()) != null)
            {

                if(line.trim().replace(" ","").length()>0)
                {
                    response += line + "\n";
                }
            }
           // Log.d(TAG,response);
            JSONObject jsonObject=new JSONObject(response);


            JSONArray jsonArray=jsonObject.getJSONArray("items");

           final ArrayList<String> words=new ArrayList<String>();
         for(int i=0;i<jsonArray.length();i++)
         {
             JSONObject items=jsonArray.getJSONObject(i);

            // Log.d(TAG,items.getString("words").toString());
             words.add(items.getString("words").toString().trim());
         }


            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    WordsListAdapter adapter=new WordsListAdapter(getApplicationContext(),words);
                    topWordsListView.setAdapter(adapter);
                }
            });

        }catch(Exception e)
        {

        }
    }


}
