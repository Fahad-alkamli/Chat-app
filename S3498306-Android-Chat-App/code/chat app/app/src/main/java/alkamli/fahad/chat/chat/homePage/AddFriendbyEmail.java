package alkamli.fahad.chat.chat.homePage;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import alkamli.fahad.chat.chat.R;
import alkamli.fahad.chat.chat.model.CommonFunctions;

public class AddFriendbyEmail extends AppCompatActivity {

    final String TAG="Alkamli";
    Activity activity=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friendby_email);
        activity=this;
    }

    public void addFriend(final View view)
    {
        view.setEnabled(false);
        try {
            //
            final EditText emailEditText = (EditText) findViewById(R.id.email);
            if (emailEditText.getText().toString().trim().replace(" ", "").length() < 1 || emailEditText.getText().toString().trim().replace(" ", "").contains("@") == false) {
                Toast.makeText(this, "Please type an email address.", Toast.LENGTH_LONG).show();
                return;
            }

            //let's hide content view and show the login progress bar


           final View content1=findViewById(R.id.Content1);
            content1.setVisibility(View.GONE);

            final View content2= findViewById(R.id.Content2);
            content2.setVisibility(View.GONE);

            final View loading= findViewById(R.id.loading);
            final View addFriendError=   activity.findViewById(R.id.couldntAddFriend);


            loading.setVisibility(View.VISIBLE);
            final   TextView Errorview = (TextView) activity.findViewById(R.id.emailErrorTextView);
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(emailEditText.getText().toString().trim().replace(" ", ""));
                    temp = CommonFunctions.doesFriendExists(temp, activity);
                    if (temp == null)
                    {
                        Log.d(TAG, "Friend doesn't exist");

                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {

                                try {
                                    addFriendError.setVisibility(View.INVISIBLE);
                                    Errorview.setVisibility(View.VISIBLE);
                                   content1.setVisibility(View.VISIBLE);
                                   content2.setVisibility(View.VISIBLE);
                                   loading.setVisibility(View.GONE);
                                    view.setEnabled(true);
                                }catch(Exception e)
                                {
                                    Log.d(TAG,e.getMessage());
                                }
                            }

                        });

                    } else
                    {

                        Log.d(TAG, "Friend exist ");
                        //First we send a friend request
                     final boolean addFriendStatus=  CommonFunctions.addFriends(temp,activity);
                        //Then we notify the user and close the activity
                        Log.d(TAG, "Here4");
                     activity.runOnUiThread(new Runnable()
                     {
                         @Override
                         public void run()
                         {
                             Log.d(TAG, "Here5");
                             if(addFriendStatus)
                             {
                                 Log.d(TAG, "Here6");
                                 Toast.makeText(getApplicationContext(), "Friend has been added", Toast.LENGTH_LONG).show();
                                 finish();
                             }else{
                                 Log.d(TAG, "Here7");
                                 //We couldn't add the friend so stop the progress bar and show a Toast
                                 Errorview.setVisibility(View.INVISIBLE);
                                 content1.setVisibility(View.VISIBLE);
                                 content2.setVisibility(View.VISIBLE);
                                 loading.setVisibility(View.GONE);
                                 //Show the error couldntAddFriend
                                 addFriendError.setVisibility(View.VISIBLE);
                                 Toast.makeText(getApplicationContext(), "Error: we couldn't add the friend", Toast.LENGTH_LONG).show();
                                 view.setEnabled(true);
                             }
                         }
                     });
                    }
                }
            };
            new Thread(run).start();

        }catch(Exception e)
        {
            Log.d(TAG,e.toString());
        }

    }
}
