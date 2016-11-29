package alkamli.fahad.chat.chat.homePage.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import alkamli.fahad.chat.chat.R;
import alkamli.fahad.chat.chat.homePage.home;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {


    public Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            ActionBar actionBar= home.getActionBarHome();
            actionBar.setTitle("Chat");

        }
    }


}
