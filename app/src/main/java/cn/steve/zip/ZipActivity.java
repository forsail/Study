package cn.steve.zip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hzy.lib7z.Un7Zip;

import cn.steve.study.R;

/**
 * Created by steveyan on 6/15/17.
 * Email:tinggengyan@gmail.com
 */

public class ZipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        findViewById(R.id.buttonMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = "sdcard/lvmama/lvyuepage.7z";
                String outPath = "sdcard/lvmama";
                Un7Zip.extract7z(filePath, outPath);
            }
        });
    }
}
