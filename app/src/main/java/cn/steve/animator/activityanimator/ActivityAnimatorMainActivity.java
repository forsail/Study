package cn.steve.animator.activityanimator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cn.steve.study.R;

/**
 * this demo copy from http://blog.csdn.net/wl9739/article/details/52833668
 *
 * Created by yantinggeng on 2016/11/3.
 */

public class ActivityAnimatorMainActivity extends AppCompatActivity {

    private Button buttonPre;
    private Button buttonLollipop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_animator);
        assignViews();
        setListener();
    }

    private void setListener() {
        buttonLollipop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityAnimatorMainActivity.this, LollipopAnimatorActivity.class);
                startActivity(intent);
            }
        });
        buttonPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityAnimatorMainActivity.this, PreLollipopAnimatorActivity.class);
                startActivity(intent);
            }
        });
    }

    private void assignViews() {
        buttonPre = (Button) findViewById(R.id.buttonPre);
        buttonLollipop = (Button) findViewById(R.id.buttonLollipop);
    }


}
