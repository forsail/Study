package cn.steve.activityresult;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.steve.study.R;

/**
 * Created by SteveYan on 2017/6/16.
 */

public class ActivityResultActivityB extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        findViewById(R.id.buttonMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(ActivityResultActivity.RESULTCODE);
                ActivityResultActivityB.this.finish();
            }
        });
    }
}
