package alkamli.fahad.chat.chat.Chat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import alkamli.fahad.chat.chat.Classes.Message;
import alkamli.fahad.chat.chat.R;

public class ChatAdapter extends ArrayAdapter<Message> {

    public ChatAdapter(Context context, List<Message> Messages)
    {
        super(context, R.layout.chat_entry, Messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=LayoutInflater.from(getContext());
        View CustomView=inflater.inflate(R.layout.chat_entry,parent,false);
        Message SingleMessage=getItem(position);

        TextView message=(TextView) CustomView.findViewById(R.id.messageBox);
        message.setText(SingleMessage.getMessage());

        if(SingleMessage.isMe())
        {
            //Green if it's me http://stackoverflow.com/questions/23517879/set-background-color-programmatically
            CustomView.setBackgroundColor(Color.parseColor("#3e2723"));

        }else{
            CustomView.setBackgroundColor(Color.parseColor("#757575"));
        }
        return CustomView;
    }
}
