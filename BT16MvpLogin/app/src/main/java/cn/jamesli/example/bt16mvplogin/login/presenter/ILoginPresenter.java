package cn.jamesli.example.bt16mvplogin.login.presenter;

/**
 * Created by jamesli on 15/11/12.
 */
public interface ILoginPresenter {
    void clearEditText();
    void doLogin(String username, String password);
    void setProgressBarVisibility(int visibility);
}
