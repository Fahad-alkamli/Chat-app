package alkamli.fahad.chat.chat.AddFriendFromContact;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import alkamli.fahad.chat.chat.R;


public class CustomContactListAdapter extends ArrayAdapter<String>{

    /*
    This adapter will handle a list of picked contacts and insert them to one large list of contacts.
     */
    public CustomContactListAdapter(Context context, ArrayList<String> names) {

        super(context, R.layout.contact_item_list,names);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View CustomView = inflater.inflate(R.layout.contact_item_list, parent, false);
            String singleItem = getItem(position);
            TextView name = (TextView) CustomView.findViewById(R.id.nameTextView);
            name.setText(singleItem);
            return CustomView;
        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }
       return null;
    }
}
