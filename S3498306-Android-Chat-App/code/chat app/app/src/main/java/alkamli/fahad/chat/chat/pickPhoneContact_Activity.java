package alkamli.fahad.chat.chat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

import alkamli.fahad.chat.chat.AddFriendFromContact.CustomContactaAdapter;
import alkamli.fahad.chat.chat.AddFriendFromContact.pickContactWindow_Controller;
import alkamli.fahad.chat.chat.model.CommonFunctions;

public class pickPhoneContact_Activity extends AppCompatActivity {
    final String TAG="Alkamli";

    public static Activity activity;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    ArrayList<String> selectedNames=new ArrayList<String>();
    private  static ListView contactsListView;

    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;
        setContentView(R.layout.pick_contact_window);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
       contactsListView=(ListView)findViewById(R.id.contactsListView);


        //Request the  android.permission.READ_CONTACTS
        int permissionCheck = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_CONTACTS);

        if (PackageManager.PERMISSION_DENIED == permissionCheck)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }else
        {

            Runnable run=new Runnable()
            {
                @Override
                public void run()
                {
                    displayContactList();
                }
            };
                    new Thread(run).start();

        }


        //Set Listener
        findViewById(R.id.getNamesButton).setOnClickListener(new pickContactWindow_Controller(this));

        if(mLoginFormView ==null || mProgressView==null)
        {
            Log.d(TAG,"One is null");
        }
        showProgress(true);
    }

    //http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/
    //This function will query the database to get all the contacts
    private void displayContactList()
    {
       final ArrayList<String> names=new ArrayList<String>();
        final ArrayList<String> numbers=new ArrayList<String>();
        try {
            Log.d(TAG,"displayContactList");
            ContentResolver ContentRe = getContentResolver();
            Cursor cur = ContentRe.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cur.getCount() > 0)
            {
                Log.d(TAG,Integer.toString(cur.getCount()));
                while (cur.moveToNext())
                {

                    final String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {

                         String number = getPhoneNumber(name);
                        if(number !=null)
                        {
                            number = number.replace("(", "").replace(")", "").replace("+", "").trim().replace("-", "").replace(" ", "");
                            if (number.length() >= 9) {
                                //prepare the phone number for search in the database get the last 9digits
                                number = number.substring(number.length() - 9);
                                names.add(name);
                                numbers.add(number);
                            }

                        }

                    }
                }
                //Send the numbers for check
                sendNumbers(numbers,names);



            }else{
                Log.d(TAG, "Couldn't find anything temp = null");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        Toast.makeText(activity,"Error: You don't have any contacts",Toast.LENGTH_LONG).show();
                        showProgress(false);}
                });
            }
        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());


        }

    }


    //https://developer.android.com/training/permissions/requesting.html#perm-request
    //Asking for permission to access the contact list
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG,"permission was granted");
                    Runnable run=new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            displayContactList();
                        }
                    };
                    new Thread(run).start();
                } else
                {
                    Toast.makeText(this, "Error: This feature needs Access to your Contacts", Toast.LENGTH_LONG).show();
                    Log.d(TAG,"permission denied");
                }
                return;
            }
        }
    }



    private String getPhoneNumber(String NAME)
    {
        //
//  Find contact based on name.
//
        String number=null;
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                "DISPLAY_NAME = '" + NAME + "'", null, null);
        if (cursor.moveToFirst()) {
            String contactId =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //
            //  Get all phone numbers.
            //
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phones.moveToNext()) {
                 number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        // do something with the Home number here...
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        // do something with the Mobile number here...
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        // do something with the Work number here...
                        break;
                }
                return number;
            }
            phones.close();
        }
        cursor.close();
        return null;
    }


    private void sendNumbers(final ArrayList<String> numbers,final ArrayList<String> names)
    {
        Runnable run = new Runnable() {
            @Override
            public void run() {

                try {
                    final ArrayList<String> temp = CommonFunctions.doesFriendExists(numbers,activity);
                    if (temp == null)
                    {
                       // showProgress(false);
                        Log.d(TAG, "Couldn't find anything temp = null");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(activity,"Error: couldn't retrieve possible contacts.",Toast.LENGTH_LONG).show();
                                showProgress(false);}
                        });
                        return;
                    }
                    if (temp.size() > 0)
                    {

                        final ArrayList<String> names2 = new ArrayList<String>();
                        for (String number : temp)
                        {
                            Log.d(TAG,"Fahad Check this "+Integer.toString(numbers.size()));
                            int count = 0;
                            for (String number2 : numbers)
                            {
                                Log.d(TAG,number.substring(number.length()-9));
                                if (number.substring(number.length()-9).contains(number2))
                                {
                                   // Log.d(TAG,"Here");
                                    Log.d(TAG,"Number 1: "+number.substring(number.length()-9));
                                   Log.d(TAG,"Compare : "+number2);
                                   // Log.d(TAG,names.get(count));
                                    names2.add(names.get(count));
                                    break;

                                }
                                count++;
                            }

                        }
                        //Now i have a list of names and numbers
                        //
                   activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {

                            ListAdapter adapt = new CustomContactaAdapter(activity, names2,temp);
                            contactsListView.setAdapter(adapt);

                           showProgress(false);
                        }
                    });

                    }
                }catch(Exception e)
                {
                   // Log.d(TAG,e.getMessage());
                    class Local {};  Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" "+e.getMessage());
                }

            }
        };

        new Thread(run).start();

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
