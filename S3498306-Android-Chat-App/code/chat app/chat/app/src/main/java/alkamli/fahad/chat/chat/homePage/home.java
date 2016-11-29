package alkamli.fahad.chat.chat.homePage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.*;
import java.io.Serializable;
import java.util.ArrayList;

import alkamli.fahad.chat.chat.Classes.Friend;
import alkamli.fahad.chat.chat.R;
import alkamli.fahad.chat.chat.Services.FetchNewMessagesService;
import alkamli.fahad.chat.chat.adminControl.adminControlPanel;
import alkamli.fahad.chat.chat.homePage.fragments.*;
import alkamli.fahad.chat.chat.pickPhoneContact_Activity;
import alkamli.fahad.chat.chat.settings.Settings;
import alkamli.fahad.chat.chat.welcomePage_Activity;

public class home extends AppCompatActivity{

   static ActionBar actionBar=null;
    final static String TAG="Alkamli";
    static String menuTitle="";
    static Toolbar toolbar;
    static MenuInflater inflater;
    TabLayoutNew tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    final static int requestCode=1;
    public static Activity activity=null;
    private  static View mProgressView;
    private static View mLoginFormView;

    private static ArrayList<Friend> friendsList =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG,"onCreate");
        setContentView(R.layout.home);
        activity=this;
        invalidateOptionsMenu();
        mProgressView=findViewById(R.id.login_progress);
        mLoginFormView=findViewById(R.id.homePageContent);
        toolbar= (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        inflater= getMenuInflater();
        actionBar=getSupportActionBar();
        tabLayout=(TabLayoutNew) findViewById(R.id.tabLayout);
        viewPager=(ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());


        viewPagerAdapter.addFragments(new Friends(),"");
        //viewPagerAdapter.addFragments(new Chat(),"Chat");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        startService(new Intent(this, FetchNewMessagesService.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
       // Log.d(TAG,"onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
       //I need to see if this is an admin or not and if he/she is then give them the extra features using a option menu
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);

        if(sharedPref.getBoolean("admin",false)!=false)
        {
            inflater.inflate(R.menu.admin_menu, menu);
        }else{
            inflater.inflate(R.menu.menu_list, menu);
        }


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // Log.d(TAG,"Option selected");

        switch(item.getItemId())
        {
            case R.id.addFriendButton:
                //Show the menu
            {
                //http://stackoverflow.com/questions/14729592/show-popup-menu-on-actionbar-item-click

                PopupMenu menu = new PopupMenu(activity, findViewById(R.id.addFriendButton));
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.pupup_menu,menu.getMenu());
                if(menu ==null)
                {

                }
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem)
                    {
                        switch(menuItem.getItemId())
                        {
                            case R.id.addByEmail:
                               // Log.d(TAG,"addByEmail");
                            {
                                Intent i = new Intent(getApplicationContext(), AddFriendbyEmail.class);
                                startActivity(i);
                            }
                                break;
                            case R.id.addFromContacts:
                               // Log.d(TAG,"addFromContacts");
                            {
                                Intent i = new Intent(activity.getBaseContext(), pickPhoneContact_Activity.class);
                                activity.startActivity(i);
                            }
                                break;

                        }
                        return true;
                    }
                });
            }
            break;

            case R.id.settingsMenuButton:
            {
                //Log.d(TAG, "Settings");
                Intent i = new Intent(this, Settings.class);
                startActivity(i);
            }
                break;
            case R.id.logout:
            {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(this, welcomePage_Activity.class);
                startActivity(i);
                finish();
            }
                break;
            case R.id.adminPanel:
            {
                Intent i=new Intent(this,adminControlPanel.class);
                startActivity(i);
            }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static ActionBar getActionBarHome()
    {
        return actionBar;
    }

    public static Toolbar getHomeToolBar()
    {
return toolbar;
    }

    public static MenuInflater getHomeMenuInflater()
    {
                return inflater;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = activity.getResources().getInteger(android.R.integer.config_shortAnimTime);

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


    public static synchronized ArrayList<Friend> getFriendsList()
    {
        return friendsList;
    }


    public static synchronized void setFriendsList(ArrayList<Friend> friendsListTemp)
    {
         friendsList=friendsListTemp;
    }



    public void buttonClick(View view)
    {
        Log.d(TAG,"Click");
        for(Friend friend:getFriendsList())
        {
            if(friend.getEmail().equals("alkamli@hotmail.com"))
            {
                if(friend.getProfileIcon()==null)
                {
                    Log.d(TAG,"The profile picture is null");
                }
            }
        }
    }
}

