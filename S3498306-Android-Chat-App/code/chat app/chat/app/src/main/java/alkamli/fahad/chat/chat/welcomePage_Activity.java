package alkamli.fahad.chat.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import alkamli.fahad.chat.chat.homePage.home;
import alkamli.fahad.chat.chat.model.CommonFunctions;

public class welcomePage_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);

        if(CommonFunctions.alreadyLoggedIn(this))
        {
            CommonFunctions.sendToast(this,"Welcome Back !");
            Intent i=new Intent(this,home.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.welcome_page);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    }

    public void login(View view)
    {
        Intent i=new Intent(this,Login_Activity.class);
        startActivity(i);
    }

    public void register(View view)
    {
        Intent i=new Intent(this,Register_Activity.class);
        startActivity(i);
    }
}
