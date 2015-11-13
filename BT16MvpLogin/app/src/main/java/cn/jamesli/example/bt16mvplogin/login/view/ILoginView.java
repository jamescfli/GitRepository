package cn.jamesli.example.bt16mvplogin.login.view;

/**
 * Created by jamesli on 15/11/12.
 */
public interface ILoginView {
    void clearEditText();
    void showLoginResult(boolean isLoginSuccess);
    void setProgressBarVisibility(int visibility);
}
