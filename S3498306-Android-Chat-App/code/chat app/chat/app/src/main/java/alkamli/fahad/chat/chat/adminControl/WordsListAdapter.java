package alkamli.fahad.chat.chat.adminControl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import alkamli.fahad.chat.chat.R;

/**
 * Created by d0l1 on 09-19-2016.
 */
public class WordsListAdapter extends ArrayAdapter<String> {

    public WordsListAdapter(Context context, ArrayList<String> wordsList) {

        super(context, R.layout.word_template,wordsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=LayoutInflater.from(getContext());
        View CustomView=inflater.inflate(R.layout.word_template,parent,false);


        TextView wordTextview=(TextView)CustomView.findViewById(R.id.wordTextView);
        wordTextview.setText(getItem(position));

        return CustomView;

    }
}
