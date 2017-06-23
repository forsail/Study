package cn.steve.coordinatorlayout;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import cn.steve.study.R;
import cn.steve.viewpager.ViewPagerAdapter;

/**
 * Created by yantinggeng on 2015/11/26.
 */
public class CoordinatorLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinatorlayout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add("Page" + i);
        }
        if (viewPager != null) {
            viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), data));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

    }
}
