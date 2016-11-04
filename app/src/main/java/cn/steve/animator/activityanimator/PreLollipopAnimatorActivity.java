package cn.steve.animator.activityanimator;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.steve.study.R;

/**
 * Created by yantinggeng on 2016/11/3.
 */

public class PreLollipopAnimatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lollipop_image);
    }

    public void imageClick(View view) {
        CustomImage customImage = (CustomImage) view;
        Intent intent = new Intent(this, PreLollipopAnimatorDetailActivity.class);
        // 创建一个 rect 对象来存储共享元素位置信息
        Rect rect = new Rect();
        // 获取元素位置信息
        view.getGlobalVisibleRect(rect);
        // 将位置信息附加到 intent 上
        intent.setSourceBounds(rect);
        intent.putExtra(PreLollipopAnimatorDetailActivity.EXTRA_IMAGE, customImage.getImageId());
        startActivity(intent);
        // 屏蔽 Activity 默认转场效果
        overridePendingTransition(0, 0);
    }

}
