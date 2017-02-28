package com.lvmama.module_mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.steve.NameGenerate;

/**
 * Created by Steve on 2017/2/27.
 */

@NameGenerate
public class MineProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_profile);
    }
}
