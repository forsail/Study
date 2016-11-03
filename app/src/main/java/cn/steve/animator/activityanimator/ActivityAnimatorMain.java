package cn.steve.animator.activityanimator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cn.steve.study.R;

/**
 * Created by yantinggeng on 2016/11/3.
 */

public class ActivityAnimatorMain extends AppCompatActivity {

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
            public void onC0lick(View v) {
                Intent intent = new Intent(ActivityAnimatorMain.this, LollipopAnimatorActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(ActivityAnimatorMain.this).toBundle();
                startActivity(intent, bundle);
            }
        });
    }

    private void assignViews() {
        buttonPre = (Button) findViewById(R.id.buttonPre);
        buttonLollipop = (Button) findViewById(R.id.buttonLollipop);
    }


}
