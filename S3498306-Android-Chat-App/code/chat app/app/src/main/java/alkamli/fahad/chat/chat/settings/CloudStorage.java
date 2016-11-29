package alkamli.fahad.chat.chat.settings;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageRequest;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.StorageObject;

import java.io.*;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import alkamli.fahad.chat.chat.R;

public class CloudStorage {

    static Activity activity=null;
    //http://stackoverflow.com/questions/18002293/uploading-image-from-android-to-gcs

    static Storage storage=null;
    public static String uploadFile(Activity activity2,String bucketName, String filePath)
    {
        activity=activity2;
        try {
            Storage storage = getStorage();
            StorageObject object = new StorageObject();
            object.setBucket(bucketName);
            File sdcard = Environment.getExternalStorageDirectory();
            String path=sdcard+"/profilePicture.jpg";
            FileOutputStream out = null;
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            Bitmap scaledBitmap = scaleDown(bmp, 300, true);

            Log.d("Alkamli",path);
            try {
                out = new FileOutputStream(path);
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            File file = new File(path);

            InputStream stream = new FileInputStream(file);

            try {
                Log.d("Alkamli","Test");
                String contentType = URLConnection.guessContentTypeFromStream(stream);
                InputStreamContent content = new InputStreamContent(contentType, stream);


                Storage.Objects.Insert insert = storage.objects().insert(bucketName, null, content);
                SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.file_key), Context.MODE_PRIVATE);



                insert.setPredefinedAcl("publicread");
                String extension = "";

                int i = file.getName().lastIndexOf('.');
                if (i > 0) {
                    extension = file.getName().substring(i+1);
                }
                String path2="profilePicture-"+sharedPref.getString("email",null)+"."+extension;
                Log.d("Alkamli",path);
                insert.setName(path2);

                insert.execute();

                stream.close();

                return "https://storage.googleapis.com/chat-api-s3498306.appspot.com/"+path2;

            } finally {
                stream.close();
            }

        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

            e.printStackTrace();
        }
        return null;
    }

    private static Storage getStorage() {

        try {

            if (storage == null)
            {
                HttpTransport httpTransport = new NetHttpTransport();
                JsonFactory jsonFactory = new JacksonFactory();
                List<String> scopes = new ArrayList<String>();
                scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);

                Credential credential = new GoogleCredential.Builder()
                        .setTransport(httpTransport)
                        .setJsonFactory(jsonFactory)
                        .setServiceAccountId("testing@chat-api-s3498306.iam.gserviceaccount.com") //Email
                        .setServiceAccountPrivateKeyFromP12File(getTempPkc12File())
                        .setServiceAccountScopes(scopes).build();

                storage = new Storage.Builder(httpTransport, jsonFactory,
                        credential)
                        .build();
            }

            return storage;
        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }
        Log.d("Alkamli","Storage object is null ");
        return null;
    }

    private static File getTempPkc12File() {
        try {
            // xxx.p12 export from google API console
            InputStream pkc12Stream = activity.getResources().getAssets().open("chat-API-s3498306-d77f5d4f0f5c.p12");
            File tempPkc12File = File.createTempFile("temp_pkc12_file", "p12");
            OutputStream tempFileStream = new FileOutputStream(tempPkc12File);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = pkc12Stream.read(bytes)) != -1) {
                tempFileStream.write(bytes, 0, read);
            }
            return tempPkc12File;
        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }
        Log.d("Alkamli"," getTempPkc12File is null");
        return null;
    }


    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}
