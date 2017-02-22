package cn.steve.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.steve.study.R;

/**
 * Created by Steve on 2017/2/22.
 */

public class TaskActivityA extends AppCompatActivity {

    private Button buttonMain;
    private TextView textView;

    private void assignViews() {
        buttonMain = (Button) findViewById(R.id.buttonMain);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("跳转到B页面");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        assignViews();
        buttonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivityA.this, TaskActivityB.class);
                TaskActivityA.this.startActivity(intent);
            }
        });
    }
}
