package cn.steve.activityresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cn.steve.study.R;

/**
 * Created by SteveYan on 2017/6/16.
 */

public class ActivityResultActivity extends AppCompatActivity {

    public static final int REQUESTCODE = 10000;
    public static final int RESULTCODE = 10001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        findViewById(R.id.buttonMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityResultActivity.this, ActivityResultActivityB.class);
                // 若启动方式为 newtask ，则在启动前会调用onActivityResult
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityResultActivity.this.startActivityForResult(intent, REQUESTCODE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULTCODE) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        }
    }
}
