package cn.jamesli.example.bt16mvplogin.login.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.jamesli.example.bt16mvplogin.R;
import cn.jamesli.example.bt16mvplogin.login.presenter.ILoginPresenter;
import cn.jamesli.example.bt16mvplogin.login.presenter.LoginPresenterCompl;


public class LoginViewActivity extends AppCompatActivity implements ILoginView {
    private EditText mEditTextUsername;
    private EditText mEditTextPassword;
    private Button mButtonLogin;
    private Button mButtonClear;
    private ProgressBar mProgressBar;

    // task presenter for logic process
    private ILoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initiateUi();

        // set button listener
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginPresenter.setProgressBarVisibility(View.VISIBLE);
                mButtonLogin.setEnabled(false);
                mButtonClear.setEnabled(false);
                mLoginPresenter.doLogin(mEditTextUsername.getText().toString(), mEditTextPassword.getText().toString());
            }
        });
        mButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginPresenter.clearEditText();
            }
        });

        // set Presenter
        mLoginPresenter = new LoginPresenterCompl(this);
        mLoginPresenter.setProgressBarVisibility(View.INVISIBLE);
    }

    private void initiateUi() {
        mEditTextUsername = (EditText) findViewById(R.id.edit_text_un);
        mEditTextPassword = (EditText) findViewById(R.id.edit_text_pw);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mButtonClear = (Button) findViewById(R.id.button_clear);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void clearEditText() {
        mEditTextUsername.setText("");
        mEditTextPassword.setText("");
    }

    @Override
    public void showLoginResult(boolean isLoginSuccess) {
        mLoginPresenter.setProgressBarVisibility(View.INVISIBLE);
        mButtonLogin.setEnabled(true);
        mButtonClear.setEnabled(true);
        if (isLoginSuccess) {
            Toast.makeText(this, "Login passed!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        mProgressBar.setVisibility(visibility);
    }
}
