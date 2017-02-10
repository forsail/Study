package cn.steve.recyclerview.fixed;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ScrollView;

import cn.steve.study.R;

/**
 * Created by Steve on 2017/2/7.
 */

public class FixedRecyclerViewActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private Toolbar toolBar;
    private TabLayout tabLayout;
    private TabLayout topTabLayout;
    private ViewPager viewPager;

    private void assignViews() {
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        topTabLayout = (TabLayout) findViewById(R.id.topTabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixedrecyclerview);
        assignViews();
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            }
        });


    }
}
