package alkamli.fahad.chat.chat.homePage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import alkamli.fahad.chat.chat.Chat.Chat_Template;
import alkamli.fahad.chat.chat.Classes.Friend;
import alkamli.fahad.chat.chat.R;
import alkamli.fahad.chat.chat.homePage.home;
import alkamli.fahad.chat.chat.model.CommonFunctions;

public class FriendsAdapter extends ArrayAdapter<Friend> {


    public FriendsAdapter(Context context, ArrayList<Friend> friendsList) {

        super(context, R.layout.friend_template,friendsList);
        //Log.d("Alkamli","Adapter call");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Log.d("Alkamli","getView");
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.friend_template, parent, false);
        try {

            TextView title = (TextView) customView.findViewById(R.id.friend_title);

            ImageView image = (ImageView) customView.findViewById(R.id.friend_image);

            RelativeLayout layout=(RelativeLayout)  customView.findViewById(R.id.friend_template);

            //Start a chat with a click on the friend's name
            layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String friendTitle=((TextView) view.findViewById(R.id.friend_title)).getText().toString();
                    Intent i=new Intent(getContext(), Chat_Template.class);
                    i.putExtra("to",((String)view.getTag()));
                    i.putExtra("title",friendTitle);
                    getContext().startActivity(i);



                }
            });


            //Delete a friend
            layout.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(final View view) {
                  //  Log.d("Alkamli","Long click");

                    PopupMenu popupMenu = new PopupMenu(home.activity, view);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem)
                        {
                              //  Log.d("Alkamli","Clicked");
                                final String friend_email=(String)view.getTag();
                           // Log.d("Alkamli",friend_email);

                            Runnable run=new Runnable(){
                                @Override
                                public void run() {
                                    CommonFunctions.deleteFriend(home.activity,friend_email);
                                }
                            };
                            new Thread(run).start();
                            return true;
                        }
                    });
                    popupMenu.inflate(R.menu.delete_friend_menu);
                    popupMenu.show();
                    return true;
                }
            });


            Friend friend = getItem(position);
            title.setText(friend.getName());

           // email.setText(friend.getEmail());
            //Log.d("Alkamli","Test: "+friend.getEmail());
            layout.setTag(friend.getEmail());
            //http://stackoverflow.com/questions/5853167/runnable-with-a-parameter
            class SetImage implements Runnable {
                ImageView image;
                String url;
                String email;
                public SetImage(ImageView tempImage, String url,String email)
                {
                    this.image = tempImage;
                    this.url = url;
                    this.email=email;
                   // Log.d("Alkamli","FRiend's picture:"+url);

                }

                @Override
                public void run() {
                    final Bitmap temp = CommonFunctions.getImage(url);

                    if (temp == null) {
                        return;
                    }
                    home.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            image.setImageBitmap(temp);

                            for(Friend friend:home.getFriendsList())
                            {
                                if(friend.getEmail().toLowerCase().trim().equals(email.trim().toLowerCase()))
                                {
                                    friend.setProfileIcon(temp.copy(temp.getConfig(),false));
                                    Log.d("Alkamli","Done settings the friend's profile: "+friend.getEmail());
                                    if(friend.getProfileIcon()==null)
                                    {
                                        Log.d("Alkamli","Something wrong ");

                                    }
                                    break;
                                }

                            }

                        }
                    });
                }
            }

            if(friend.getPicture() != null)
            {
                Thread t = new Thread(new SetImage(image, friend.getPicture(),friend.getEmail()));
                t.start();
            }

        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }

        return customView;
    }





}
