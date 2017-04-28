package cn.steve.task.launchmodel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import cn.steve.study.R;

/**
 * Created by Steve on 2017/3/21.
 */

public class SingleTopActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        button = (Button) findViewById(R.id.buttonMain);
        textView = (TextView) findViewById(R.id.textView);

    }
}
