package cn.steve.activitylifecycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import cn.steve.study.R;


public class LifeCircleActivity extends Activity {

    TextView textView;
    String s = "first";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate");
        setContentView(R.layout.activity_main_textview);
        textView = (TextView) findViewById(R.id.textViewMain);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key", "x");
        System.out.println("onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        System.out.println("onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        String a = savedInstanceState.getString("key");
        textView.setText(a);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
        s = "暂停";
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("onRestart");
        textView.setText(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Menu");
        System.out.println("onCreateOptionsMenu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        System.out.println("onResume");
        super.onResume();
    }
}
