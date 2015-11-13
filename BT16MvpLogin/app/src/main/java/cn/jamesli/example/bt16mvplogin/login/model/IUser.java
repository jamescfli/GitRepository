package cn.jamesli.example.bt16mvplogin.login.model;

/**
 * Created by jamesli on 15/11/12.
 */
public interface IUser {
    boolean checkUserValidity(String username, String password);
}
