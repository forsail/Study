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

public class TaskActivityB extends AppCompatActivity {

    private Button buttonMain;
    private TextView textView;

    private void assignViews() {
        buttonMain = (Button) findViewById(R.id.buttonMain);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("跳转到C页面");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        assignViews();
        buttonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivityB.this, TaskActivityC.class);
                // 如若要想避免内存重启的现象，可以采用 FLAG_ACTIVITY_CLEAR_TASK 方式清空 task
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                TaskActivityB.this.startActivity(intent);
            }
        });
    }
}
