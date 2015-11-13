package cn.jamesli.example.bt16mvplogin.login.presenter;

import android.os.Handler;
import android.os.Looper;

import cn.jamesli.example.bt16mvplogin.login.model.IUser;
import cn.jamesli.example.bt16mvplogin.login.model.UserModel;
import cn.jamesli.example.bt16mvplogin.login.view.ILoginView;

/**
 * Created by jamesli on 15/11/12.
 */
public class LoginPresenterCompl implements ILoginPresenter {
    ILoginView iLoginView;
    IUser iUser;
    Handler mHandler;

    public LoginPresenterCompl(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
        initOneUser();  // set an arbitrary user
        mHandler = new Handler(Looper.getMainLooper());
    }

    private void initOneUser() {
        iUser = new UserModel("mvp", "mvp");
    }

    @Override
    public void clearEditText() {
        iLoginView.clearEditText();
    }

    @Override
    public void doLogin(String username, String password) {
        final boolean result = iUser.checkUserValidity(username, password);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iLoginView.showLoginResult(result);
            }
        }, 3000);
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        iLoginView.setProgressBarVisibility(visibility);
    }
}
