package alkamli.fahad.chat.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import alkamli.fahad.chat.chat.homePage.home;
import alkamli.fahad.chat.chat.model.CommonFunctions;
import alkamli.fahad.chat.chat.Classes.UserInfo;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Register_Activity extends AppCompatActivity {

    final String TAG="Alkamli";

    public static Activity activity=null;
    static View registerContent1=null;
    static  View registerContent2=null;
    static ProgressBar mProgressView=null;
    UserInfo user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//Check if the user already logged in
        if(CommonFunctions.alreadyLoggedIn(this))
        {
            CommonFunctions.sendToast(activity,"Welcome Back !");
            Intent i=new Intent(this,home.class);
            startActivity(i);
        }
        setContentView(R.layout.register);

        activity=this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void createUser(View view)
    {
        Log.d(TAG,"Test");
        if(!validateFields())
        {
            return;
        }

        registerContent1=findViewById(R.id.registerContent1);
        registerContent2=findViewById(R.id.registerContent2);
        mProgressView=(ProgressBar) findViewById(R.id.login_progress);

      final  String email=((EditText)findViewById(R.id.email)).getText().toString();
       final String password=((EditText)findViewById(R.id.password)).getText().toString();
      final  String number=((EditText)findViewById(R.id.phone_number)).getText().toString();


        showProgress(true);
        Runnable run=new Runnable()
        {
            @Override
            public void run() {
                Looper.prepare();
                boolean message;

                message=   CommonFunctions.Register(email,password,number);
                if(message)
                {
                    Log.d(TAG," The user registered successfully");


                    boolean LoggedIn= CommonFunctions.Login(activity,email,password);
                    if(LoggedIn)
                    {
                        CommonFunctions.sendToast(activity,"Welcome !");
                        Intent i=new Intent(activity,home.class);
                        startActivity(i);

                    }else{
                        //Login_Activity process failed;
                        CommonFunctions.sendToast(activity,"Error: Login_Activity Failed !");

                    }
                }else{
                    Log.d(TAG,"Error: registration was Unsuccessful");
                    CommonFunctions.sendToast(activity,"Error: registration was Unsuccessful");
                }
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        showProgress(false);
                    }
                });
            }
        };
            new Thread(run).start();

    }


    private boolean validateFields()
    {
        //Now we get both the email and the password fields and validate them

        String email=((EditText)findViewById(R.id.email)).getText().toString();
        String password=((EditText)findViewById(R.id.password)).getText().toString();
        String number=((EditText)findViewById(R.id.phone_number)).getText().toString();
        if(CommonFunctions.isEmailValid(email.trim()) != CommonFunctions.errors.Good)
        {
            DisplayError(CommonFunctions.isEmailValid(email.trim()),"Email");
            return false;
        }
        if(CommonFunctions.isPasswordValid(password.trim()) != CommonFunctions.errors.Good)
        {
            DisplayError(CommonFunctions.isPasswordValid(password.trim()),"Password");
            return false;
        }
        if(CommonFunctions.isPhoneNumberValid(number.trim()) != CommonFunctions.errors.Good)
        {
            DisplayError(CommonFunctions.isPhoneNumberValid(number.trim()),"Phone Number");

            return false;
        }
        return true;
    }
    private void DisplayError(CommonFunctions.errors error,String place)
    {
        switch(error)
        {
            case MissingAt:
                Toast.makeText(this,"Error: email is missing @ symbol",Toast.LENGTH_LONG).show();
                break;
            case MissingDot:
                Toast.makeText(this,"Error: email is missing . symbol",Toast.LENGTH_LONG).show();
                break;
            case MissingCountryCode:
                Toast.makeText(this,"Error: Phone Number is missing the Country Code",Toast.LENGTH_LONG).show();
                break;
            case ShortLength:
                Toast.makeText(this,"Error:  "+place+" is too short.",Toast.LENGTH_LONG).show();
                break;
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            registerContent1.setVisibility(show ? View.GONE : View.VISIBLE);
            registerContent1.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerContent1.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            registerContent2.setVisibility(show ? View.GONE : View.VISIBLE);
            registerContent2.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerContent2.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            registerContent1.setVisibility(show ? View.GONE : View.VISIBLE);
            registerContent2.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }




}
