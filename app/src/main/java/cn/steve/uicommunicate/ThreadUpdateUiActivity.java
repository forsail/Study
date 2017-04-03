package cn.steve.uicommunicate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import cn.steve.study.R;

/**
 * Created by yantinggeng on 2016/6/8.
 */
public class ThreadUpdateUiActivity extends AppCompatActivity {

    private static final String TAG = "ThreadUpdateUiActivity";
    private TextView textViewMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        textViewMain = (TextView) findViewById(R.id.textView);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 在以上所有方法中,都不会产生异常
        //newLooper4Thread();
    }

    private void newLooper4Thread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 如该加上延迟,就会报错.
                //try {
                //    Thread.sleep(3000);
                //} catch (InterruptedException e) {
                //    e.printStackTrace();
                //}
                Log.e(TAG, "run: " + Thread.currentThread().toString());
                textViewMain.setText("大写的服!!!!!!");
            }
        }).start();
    }
}
