package cn.steve.realm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import cn.steve.study.R;
import io.realm.Realm;

/**
 * Created by yantinggeng on 2016/11/4.
 */

public class RealmActivity extends AppCompatActivity {

    private Button buttonMain;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        assignViews();
    }

    private void assignViews() {
        buttonMain = (Button) findViewById(R.id.buttonMain);
        textView = (TextView) findViewById(R.id.textView);
    }

    private void createObj(){
        Realm.getDefaultInstance();
    }




}
