package cn.jamesli.example.bt16mvplogin.login.model;

/**
 * Created by jamesli on 15/11/12.
 */
public class UserModel implements IUser {
    private String username;
    private String password;

    public UserModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public String getPassword() {
//        return password;
//    }

    @Override
    public boolean checkUserValidity(String username, String password) {
        if (this.username.equals(username) && this.password.equals(password)) {
            return true;
        } else {
            return false;
        }
    }
}
