package cn.steve.floatingnavigation;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;

import cn.steve.study.R;


/**
 * Created by SteveYan on 2017/6/20.
 */

public class NavigationMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        findViewById(R.id.buttonMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }
        });
    }

    private void showPop() {
        final PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.pop_background));
        popupWindow.setBackgroundDrawable(dw);

        View popView = LayoutInflater.from(this).inflate(R.layout.layout_popwindow, null);

        RecyclerView navigationRecyclerView = (RecyclerView) popView.findViewById(R.id.navigationRecyclerView);
        ArrayList<NavigationAdapter.NavigationItem> data = new ArrayList<>();
        int size = 10;
        for (int i = 0; i < size; i++) {
            NavigationAdapter.NavigationItem item = new NavigationAdapter.NavigationItem((1 + i) + "", "第" + i + "title");
            //item.setLast(i == size - 1);
            ArrayList<String> ss = new ArrayList<>();
            for (int i1 = 0; i1 < i; i1++) {
                ss.add("景点" + i1);
            }
            item.setScenic(ss);
            data.add(item);
        }

        NavigationAdapter adapter = new NavigationAdapter(data, navigationRecyclerView);
        adapter.setItemClickListener(new NavigationAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(NavigationAdapter.NavigationItem data) {
                Toast.makeText(NavigationMainActivity.this, data.getTitle(), Toast.LENGTH_SHORT).show();
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        //创建默认的线性LayoutManager ;设置成横向的,默认为竖屏的
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        navigationRecyclerView.setLayoutManager(mLayoutManager);
        navigationRecyclerView.setAdapter(adapter);

        popupWindow.setContentView(popView);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.navigationPopWindowStyle);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.RIGHT, 0, 0);
    }


}
