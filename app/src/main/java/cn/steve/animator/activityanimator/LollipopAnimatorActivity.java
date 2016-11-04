package cn.steve.animator.activityanimator;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.steve.study.R;

/**
 * Created by yantinggeng on 2016/11/3.
 */

public class LollipopAnimatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lollipop_image);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void imageClick(View view) {
        // 此处标记的 transition_name 和下个页面显示相同元素的控件，包含了表示下个页面share element的起始位置
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view, getString(R.string.image_transition_name));

        CustomImage image = (CustomImage) view;
        Intent intent = new Intent(this, LollipopAnimatorDetailActivity.class);
        intent.putExtra(LollipopAnimatorDetailActivity.EXTRA_IMAGE, image.getImageId());
        startActivity(intent, options.toBundle());
    }
}
