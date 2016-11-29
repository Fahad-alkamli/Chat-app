package alkamli.fahad.chat.chat.homePage.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import alkamli.fahad.chat.chat.R;


public class TabLayoutNew extends TabLayout {

    public TabLayoutNew(Context context) {
        super(context);
    }

    public TabLayoutNew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabLayoutNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("Alkamli","Test TabLayoutNew");
        return super.onTouchEvent(ev);
    }


    @Override
    public void addTab(@NonNull Tab tab, boolean setSelected) {
        super.addTab(tab, setSelected);
        //Log.d("Alkamli",tab.getText().toString());
        //Log.d("Alkamli","Test");
        if (tab.getText().toString().toLowerCase().contains("friends")) {
            tab.setIcon(R.mipmap.ic_person_white_24dp);
        } else if (tab.getText().toString().toLowerCase().contains("chat"))
        {
            tab.setIcon(R.mipmap.ic_question_answer_white_24dp);
        }
    }

}

