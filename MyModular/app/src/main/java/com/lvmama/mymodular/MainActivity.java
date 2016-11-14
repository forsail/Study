package com.lvmama.mymodular;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.steve.NameGenerate;

@NameGenerate
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
