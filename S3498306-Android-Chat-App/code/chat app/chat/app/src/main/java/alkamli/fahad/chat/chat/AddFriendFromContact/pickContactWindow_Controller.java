package alkamli.fahad.chat.chat.AddFriendFromContact;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;

import alkamli.fahad.chat.chat.R;
import alkamli.fahad.chat.chat.homePage.home;
import alkamli.fahad.chat.chat.model.CommonFunctions;

public class pickContactWindow_Controller implements View.OnClickListener {
    static ArrayList<String> selectedNames=new ArrayList<String>();
   static ArrayList<String> numbers=new ArrayList<String>();

    String TAG="Alkamli";
    Activity activity;

    public pickContactWindow_Controller(Activity activity)
    {
        this.activity=activity;
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.nameCheckBox:
                CheckBox selectedName=(CheckBox) view;
                if(selectedName.isChecked())
                {
                    //selectedNames
                    selectedNames.add(selectedName.getText().toString());
                    numbers.add((String)selectedName.getTag());

                   Log.d(TAG,"Name has been added to the list");
                  // Log.d(TAG,numbers.get(0));

                }else{
                    selectedNames.remove(selectedName.getText().toString());
                    numbers.remove((String) selectedName.getTag());
                    Log.d(TAG,"Name has been removed from the list");
                }
                break;

            //This function will handle a on click button to send the picked contacts back to the event phase2
            case R.id.getNamesButton:
                //invite all the friends in the list
                    Log.d("Alkamli","Clicked");

                for(int i=0; i<selectedNames.size();i++)
                {
                    Log.d(TAG,selectedNames.get(i)+" : "+numbers.get(i));

                }
                if(numbers.size()>0)
                {
                    Runnable run=new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            CommonFunctions.addFriends(numbers,activity);
                        }
                    };
                    new Thread(run).start();

                }
                //Go back to the home
                Intent i =new Intent(activity,home.class);
                activity.startActivity(i);
                break;

            default:
                Log.d("Alkamli","Default option: "+view.getId());
        }

    }
}
