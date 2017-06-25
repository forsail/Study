package cn.steve.coordinatorlayout;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import cn.steve.recyclerview.SimpleRecyclerAdapter;
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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.coordinatorLayoutRecyclerView);

        //创建默认的线性LayoutManager ;设置成横向的,默认为竖屏的
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        SimpleRecyclerAdapter mAdapter = new SimpleRecyclerAdapter(getDummyDatas());
        mAdapter.setOnItemClickListener(new SimpleRecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                int height = view.getHeight();
                Log.i("height:", "" + height);
            }
        });
        recyclerView.setAdapter(mAdapter);

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

    private String[] getDummyDatas() {
        String[] datas = new String[200];
        for (int i = 0; i < datas.length; i++) {
            datas[i] = "Data" + i;
        }
        return datas;
    }
}
