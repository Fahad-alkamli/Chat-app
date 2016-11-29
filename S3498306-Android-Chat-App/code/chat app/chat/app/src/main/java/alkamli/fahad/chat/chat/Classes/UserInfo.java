package alkamli.fahad.chat.chat.Classes;


public class UserInfo {

   private String email;
   private String password;
   private String phone_number;
private String token;
    public UserInfo()
    {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo(String email, String password, String phone_number, String token) {
        this.email = email;
        this.password = password;
        this.phone_number = phone_number;
        this.token=token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
