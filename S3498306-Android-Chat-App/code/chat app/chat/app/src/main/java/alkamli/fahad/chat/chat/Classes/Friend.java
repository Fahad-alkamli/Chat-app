package alkamli.fahad.chat.chat.Classes;


import android.graphics.Bitmap;
import android.media.Image;

public class Friend {

    private String name;
    private String email;
    private String picture;
    private static Bitmap profileIcon;
    public Friend(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }


    public Bitmap getProfileIcon() {
        return profileIcon;
    }

    public void setProfileIcon(Bitmap profileIconTemp)
    {
        profileIcon = profileIconTemp;
    }
}
