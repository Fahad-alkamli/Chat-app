package alkamli.fahad.chat.chat.AddFriendFromContact;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;


import java.util.ArrayList;

import alkamli.fahad.chat.chat.R;


public class CustomContactaAdapter extends ArrayAdapter<String>{

/*
The customeContact adapter will insert an array of names to a contact view controller.
Each contact view controller will have a checkbox and a click on the checkbox will add the selected name to the array of names.
 */

    ArrayList<String> numbers;

    public CustomContactaAdapter(Context context, ArrayList<String> names,ArrayList<String> numbers)
    {
        super(context, R.layout.contact_item,names);
        this.numbers=numbers;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(getContext());
        View CustomView=inflater.inflate(R.layout.contact_item,parent,false);
        String singleItem=getItem(position);
        CheckBox name=(CheckBox)CustomView.findViewById(R.id.nameCheckBox);
        name.setText(singleItem);
        name.setTag(numbers.get(position));
        //Set listeners
        name.setOnClickListener(new pickContactWindow_Controller(null));
        Log.d("Alkamli",numbers.get(position));
        return CustomView;
    }
}
