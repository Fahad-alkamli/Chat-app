package alkamli.fahad.chat.chat.Classes;


import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Comparable<Message> {
    private String message,date;
    private boolean me;
    private boolean isRead=false;
    private String frendship_key;

    public Message(String message, String date, boolean me, boolean isRead, String frendship_key) {
        this.message = message;
        this.date = date;
        this.me = me;
        this.isRead=isRead;
        this.frendship_key = frendship_key;
    }

    public String getFrendship_key() {
        return frendship_key;
    }

    public void setFrendship_key(String frendship_key) {
        this.frendship_key = frendship_key;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }

    @Override
    public int compareTo(Message message) {

        DateFormat dateFormat=new SimpleDateFormat("dd/mm/yyyy HH:mm:ss.SSS");
        try {
            Date date1 = dateFormat.parse(getDate());
            Date date2 = dateFormat.parse(message.getDate());
            return date1.compareTo(date2);
        }catch(Exception e)
        {
            class Local {}; Log.d("Alkamli","Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }

        return 0;
    }
}
