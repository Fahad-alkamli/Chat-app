package alkamli.fahad.chat.chat.homePage.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import alkamli.fahad.chat.chat.Classes.Friend;
import alkamli.fahad.chat.chat.R;
import alkamli.fahad.chat.chat.homePage.FriendsAdapter;
import alkamli.fahad.chat.chat.homePage.home;
import alkamli.fahad.chat.chat.model.CommonFunctions;

/**
 * A simple {@link Fragment} subclass.
 */
public class Friends extends Fragment {

    final String TAG="Alkamli";
     static  ListView friendsList;
    static boolean stop=false;
    static ArrayList<Friend> friends=new ArrayList<Friend>();

    public Friends() {
        // Required empty public constructor
        stop=false;
    }

    View viewTemplate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        stop=false;
        viewTemplate=inflater.inflate(R.layout.fragment_friends, container, false);


        friendsList=(ListView)viewTemplate.findViewById(R.id.friendListController);
        setupGetFriendsThread();

        return viewTemplate;
    }


    private void setupGetFriendsThread()
    {


        home.showProgress(true);
        Runnable run=new Runnable()
        {
            @Override
            public void run() {

                do {
                    try {
                        if(stop)
                        {
                            Log.d(TAG,"looking for new friends thread has been terminated");
                            return;
                        }
                       // Log.d("Alkamli","Here");
                        home.setFriendsList( CommonFunctions.getFriendsList(home.activity));
                        //After the function finishes loading then show the UI
                        home.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {home.showProgress(false);}
                        });
                        //In case the user doesn't have the correct Credentials
                        if(home.getFriendsList() ==null)
                        {

                            //We shall reach this point if the API returns null
                            CommonFunctions.logout(home.activity);
                            return;
                        }
                        boolean equal=equal(friends, home.getFriendsList());
                        //Log.d(TAG,Boolean.toString(equal));
                        if(equal ||  home.getFriendsList().size()==0)
                        {
                            //The list dones't need any update
                            Log.d(TAG,"The list dones't need any update");
                            synchronized (this)
                            {
                               // Log.d(TAG,"Waiting");
                                wait(30000);
                            }
                            continue;
                        }else{
                           // Log.d(TAG,"Else");
                            friends =  home.getFriendsList();
                        }
                        // Log.d(TAG,"Thread1 Start");

                        // Log.d(TAG,"Thread1 End");
                        final ArrayAdapter adapt = new FriendsAdapter(home.activity, friends);

                        home.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               // Log.d(TAG, "Here?");

                                if(Friends.friendsList != null)
                                {
                                    Friends.friendsList.setAdapter(adapt);
                                }
                            }
                        });


                    } catch (Exception e) {
                        class Local {
                        }
                        ;
                        Log.d("Alkamli", "Sub: " + Local.class.getEnclosingMethod().getName() + " Error code: " + e.getMessage());

                    }
                }while(true);
            }

        };

        new Thread(run).start();

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        try {

            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser)
            {
               // Log.d("Alkamli","Here");
                ActionBar actionBar = home.getActionBarHome();
                actionBar.setTitle("Friends");
            }
        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }
    }



    private boolean equal(ArrayList<Friend> friendList1,ArrayList<Friend> friendList2)
    {
        try {
            if (friendList1.size() != friendList2.size()) {
               // Log.d(TAG,"Not the same size");
                return false;
            }
            if ( friendList2.size()==0) {
                //Log.d(TAG,"no friends");
                return false;
            }


            for (int i = 0; i < friendList1.size(); i++) {
                if (!friendList1.get(i).getEmail().equals(friendList2.get(i).getEmail()))
                {
                   // Log.d(TAG,friendList1.get(i).getEmail()+"   ||   "+friendList2.get(i).getEmail());
                   // Log.d(TAG,"one item doesn't equal the other");

                    return false;
                }
            }
        }catch(Exception e)
        {
            Log.d(TAG,"Exception");
            return false;
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
       // Log.d(TAG,"Fragment onResume");
        if(home.getFriendsList() != null && home.getFriendsList().size()>0)
        {
            //Log.d(TAG,"Fragment 1");
            final ArrayAdapter adapt = new FriendsAdapter(home.activity, friends);
            home.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //
                   // Log.d(TAG,"Fragment 2");

                    if(Friends.friendsList != null)
                    {
                       // Log.d(TAG, "Get the friends after resume");
                        Friends.friendsList.setAdapter(adapt);
                    }
                }
            });

        }

    }

    public static synchronized void removeFriend(String email)
    {

        Friend deleteFriend=null;
        for(Friend friend:friends)
        {
            if(friend.getEmail().trim().equals(email.trim()))
            {
                deleteFriend=friend;
                break;
            }
        }

        if(deleteFriend != null)
        {
            friends.remove(deleteFriend);
            final ArrayAdapter adapt = new FriendsAdapter(home.activity, friends);

            home.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.d("Alkamli", "Here?");

                    if(Friends.friendsList != null)
                    {
                        Friends.friendsList.setAdapter(adapt);
                    }
                }
            });

        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stop=true;
    }
}
