package cn.jamesli.example.bt15dynamicproxy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class MainActivity extends AppCompatActivity {
    private Button mButtonFoo;
    private Button mButtonBar;
    private TextView mTextViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonFoo = (Button) findViewById(R.id.buttonFoo);
        mButtonFoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusinessFooImpl bfoo = new BusinessFooImpl();
                BusinessFoo bf = (BusinessFoo) BusinessImplProxy.factory(bfoo);
                bf.foo();
            }
        });
        mButtonBar = (Button) findViewById(R.id.buttonBar);
        mButtonBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusinessBarImpl bbar = new BusinessBarImpl();
                BusinessBar bb = (BusinessBar) BusinessImplProxy.factory(bbar);
                System.out.println(bb.bar("Business Bar Implementation"));
            }
        });
        mTextViewStatus = (TextView) findViewById(R.id.text_view_status);
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

    // abstract characters, only interface supported
    private interface BusinessFoo {
        void foo();
    }

    private interface BusinessBar {
        String bar(String message);
    }

    // real characters, implementation
    private class BusinessFooImpl implements BusinessFoo {
        @Override
        public void foo() {
            mTextViewStatus.setText("BusinessFooImpl.foo()");
        }
    }

    private class BusinessBarImpl implements BusinessBar {
        @Override
        public String bar(String message) {
            mTextViewStatus.setText("BusinessFooImpl.bar()");
            return message;
        }
    }


}

// proxy implements InvocationHandler
class BusinessImplProxy implements InvocationHandler {
    private Object obj;

//    BusinessImplProxy() { }
    BusinessImplProxy(Object obj) {
        this.obj = obj;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        doBefore();
        result = method.invoke(obj, args);
        doAfter();
        return result;
    }

    public void doBefore(){
        System.out.println("do something before Business Logic");
    }
    public void doAfter(){
        System.out.println("do something after Business Logic");
    }

    public static Object factory(Object obj) {
        Class cls = obj.getClass();
        return Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new BusinessImplProxy(obj));
    }
}
