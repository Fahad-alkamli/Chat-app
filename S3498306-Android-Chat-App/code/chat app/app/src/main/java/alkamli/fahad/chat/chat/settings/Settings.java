package alkamli.fahad.chat.chat.settings;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.OutputStreamWriter;

import alkamli.fahad.chat.chat.R;
import alkamli.fahad.chat.chat.model.CommonFunctions;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Settings extends AppCompatActivity {

    final int SELECT_PICTURE=3;
    final int FILE_PERMISSION=1;
    static String real=null;
    String TAG="Alkamli";
    EditText displayName;
    EditText phoneNumber;
    EditText passwprd;
    String selectedImagePath=null;
    static String profilePictureURL=null;
    static Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        activity=this;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_PERMISSION);

        //Set the data back to normal
       final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);

             displayName = (EditText) findViewById(R.id.displayNameEditText);
             phoneNumber = (EditText) findViewById(R.id.phoneNumberEditText);

            displayName.setText(sharedPref.getString("displayName", null));
            phoneNumber.setText(sharedPref.getString("phone_number", null));

        //Check if we already have the picture link in the device and download the picture
        if(sharedPref.getString("profilePicture", null) != null)
        {
            Log.d(TAG,sharedPref.getString("profilePicture", null));

            Runnable run=new Runnable()
            {
                @Override
                public void run() {
                  final   Bitmap test= CommonFunctions.getImage(sharedPref.getString("profilePicture", null));

                    if(test==null) return;

                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            BitmapDrawable ob = new BitmapDrawable(getResources(), test);
                            ImageView imageview = (ImageView) findViewById(R.id.profileImage);
                            imageview.setBackground(ob);
                        }
                    });

                }
            };
            new Thread(run).start();



        }





    }

    //http://stackoverflow.com/questions/2169649/get-pick-an-image-from-androids-built-in-gallery-app-programmatically
    public void BrowseForPictures(View view)
    {
        boolean cool=false;
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE )== android.content.pm.PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE )== android.content.pm.PackageManager.PERMISSION_GRANTED)
            {
                cool=true;
            }
        }
        if(!cool)
        {
            Toast.makeText(this,"This feature needs to access your storage",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_PERMISSION);
            return;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);

    }


    public void saveData(final View view)
    {

        Runnable run=new Runnable()
        {
            @Override
            public void run() {
                try {
                    String template2 =  "{'displayName':'displayNamePar','email' :'emailPar','password' : 'passwordPar','phone_number' : 'phone_numberPar','token' :'tokenPar','profilePicture' : 'profilePicturePar' }";

                    String template = "{'displayName':'displayNamePar','email' :'emailPar','password' : 'passwordPar','phone_number' : 'phone_numberPar','token' :'tokenPar','profilePicture' : 'profilePicturePar' }";
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);

                    if (displayName != null && !CommonFunctions.clean(displayName.getText().toString()).equals(CommonFunctions.clean(sharedPref.getString("displayName", null)))) {
                        template = template.replace("displayNamePar", displayName.getText().toString().trim());
                    }

                    if (passwprd != null && !CommonFunctions.clean(passwprd.getText().toString()).equals(CommonFunctions.clean(sharedPref.getString("password", null)))) {
                        template = template.replace("passwordPar", passwprd.getText().toString().trim());
                    }

                    if (phoneNumber != null && !CommonFunctions.clean(phoneNumber.getText().toString()).equals(CommonFunctions.clean(sharedPref.getString("phone_number", null)))) {
                        template = template.replace("phone_numberPar", phoneNumber.getText().toString().trim());
                    }
                    String profilePicture = null;
                    if (profilePictureURL != null) {
                        Log.d(TAG, "Here!!");
                        profilePicture =profilePictureURL;

                        if (profilePicture != null)
                        {
                            template=template.replace("profilePicturePar",profilePictureURL.trim());
                        }
                    }
                    if (template.equals(template2)) {
                        Log.d(TAG, "What!!");
                      //  Log.d(TAG, template);
                        return;
                    }

                    //prepare the token and the email address
                    template = template.replace("emailPar", sharedPref.getString("email", null));
                    template = template.replace("tokenPar", sharedPref.getString("token", null));


                    //Finally i also need to clean the template for unwanted parameters.
                    if (template.contains("displayNamePar")) {

                        template = template.replace("'displayName':'displayNamePar',", "");
                    }
                    if (template.contains("passwordPar")) {
                        template = template.replace("'password' : 'passwordPar',", "");
                    }

                    if (template.contains("phone_numberPar")) {
                        template = template.replace("'phone_number' : 'phone_numberPar',", "");

                    }

                    //Everything is ready to be sent
                  Log.d(TAG, template);

                    RequestBody body = RequestBody.create(CommonFunctions.JSON, template);
                   // Log.d(TAG,template);
                    Request request = new Request.Builder()
                            .url("https://chat-api-s3498306.appspot.com/_ah/api/userendpoint/v1/user")
                           // .post(body)
                            .put(body)
                            .build();
                    OkHttpClient client = new OkHttpClient();
                    Response response = client.newCall(request).execute();

                    if(response.code()==200)
                    {
                        Log.d(TAG,"Update profile done");
                        CommonFunctions.sendToast(activity,"Done updating profile");
                    }else{
                        Log.d(TAG,"Update profile failed");
                        Log.d(TAG,response.body().string());
                    }

                }catch(Exception e)
                {
                    class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());
                }
            }

        };
        Log.d(TAG,"Clicked");
        new Thread(run).start();


    }


    private void uploadPicture()
    {

        if(real==null)
        {
            return;
        }

        Runnable run=new Runnable()
        {
            @Override
            public void run() {
                //Show the loading frame

                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {

                       RelativeLayout content=(RelativeLayout) activity.findViewById(R.id.settingsContent);
                        content.setVisibility(View.GONE);
                        ProgressBar progressBar=(ProgressBar) activity.findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
               String temp=  CloudStorage.uploadFile(activity,"chat-api-s3498306.appspot.com",real);

                if(temp ==null)
                {
                    Log.d(TAG,"Couldn't upload");
                }
                if(temp != null)
                {
                    profilePictureURL=temp;
                    Log.d("Alkamli",profilePictureURL);
                    {
                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                Bitmap test=CommonFunctions.getImage(profilePictureURL);
                                if(test == null)
                                {
                                   return;
                                }
                                BitmapDrawable temp = new BitmapDrawable(getResources(), test);
                                ImageView imageview = (ImageView) activity.findViewById(R.id.profileImage);
                                imageview.destroyDrawingCache();
                                imageview.setBackground(temp);

                                Log.d(TAG,"Profile picture has been set");

                                //Don't forget the to set this to the user profile image
                                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_key), Context.MODE_PRIVATE);

                                sharedPref.edit().putString("profilePicture",profilePictureURL).commit();

                            }
                        });

                    }

                }
                //stop the loading frame
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {

                        RelativeLayout content=(RelativeLayout) activity.findViewById(R.id.settingsContent);
                        content.setVisibility(View.VISIBLE);
                        ProgressBar progressBar=(ProgressBar) activity.findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        };
        new Thread(run).start();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE)
            {
                Uri selectedImageUri = data.getData();
                 real=getPath(selectedImageUri);
                if(real!=null)
                {
                    Log.d("Alkamli",real);
                    uploadPicture();

                }else{
                    Toast.makeText(this,"Error: Please pick a picture from gallery",Toast.LENGTH_LONG).show();
                    Log.d("Alkamli","null");
                }

            }else if (requestCode == FILE_PERMISSION)
            {

            }
        }else{
            //Denied access to storage
            if (requestCode == FILE_PERMISSION)
            {


            }

        }
    }


    public String getPath(Uri uri)
    {
        String res = null;
        try {

            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                ;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());


        }
        return res;
    }



}
