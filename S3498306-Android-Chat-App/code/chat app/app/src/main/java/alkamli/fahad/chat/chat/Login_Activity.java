package alkamli.fahad.chat.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

import alkamli.fahad.chat.chat.homePage.home;
import alkamli.fahad.chat.chat.model.CommonFunctions;

/**
 * A login screen that offers login via email/password.
 */
public class Login_Activity extends AppCompatActivity {

   static String TAG="Alkamli";

  Activity activity=this;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    boolean loginIsOnline=false;

    public void loggin(View view)
    {
        if(loginIsOnline)
        {
            return;
        }
         AutoCompleteTextView email= (AutoCompleteTextView) findViewById(R.id.email);
         EditText password=(EditText) findViewById(R.id.password);
        if(email.getText().toString().trim().replace(" ","").length()<1 || password.getText().toString().trim().replace(" ","").length()<1)
        {

            return;
        }
        Runnable run=new Runnable()
        {
            @Override
            public void run() {
                Looper.prepare();
                login();
            }
        };
        new Thread(run).start();
    }


    private void login()
    {
        if(loginIsOnline)
        {
            return;
        }
        loginIsOnline=true;
        final AutoCompleteTextView email= (AutoCompleteTextView) findViewById(R.id.email);
        final EditText password=(EditText) findViewById(R.id.password);
        // Log.d(TAG,get());
        try {
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run() {
                    showProgress(true);
                }
            });
            //try to login
            boolean LoggedIn=CommonFunctions.Login(activity,email.getText().toString(),password.getText().toString());
            if(LoggedIn)
            {
                CommonFunctions.sendToast(activity,"Welcome !");
                Intent i=new Intent(activity,home.class);
                startActivity(i);

            }else{
                Log.d(TAG,"False login");
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        showProgress(false);
                    }
                });
                CommonFunctions.sendToast(activity,"Error: Login_Activity Failed !");

            }

        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }
        loginIsOnline=false;
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }







}

