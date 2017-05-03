package cn.steve.share.simple;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cn.steve.share.ShareConstant;
import cn.steve.share.sdk.ShareCallBack;
import cn.steve.share.sdk.ShareSDK;
import cn.steve.study.R;

public class BottomSheetShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet_share);
        FloatingActionButton floatingActionButtonShare = (FloatingActionButton) findViewById(R.id.floatingActionButtonShare);
        if (floatingActionButtonShare != null) {
            floatingActionButtonShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickActionButton();
                }
            });
        }
    }

    private void onClickActionButton() {
        new ShareSDK(this)
            .setContent("simple content")
            .setTitle("simple title")
            .setWxSessionContent("wxsession content")
            .setWxSessionTitle("wxsesstion title")
            .setWxShareCallBack(new ShareCallBack() {
                @Override
                public void shareSuccess() {
                    Toast.makeText(getApplicationContext(), "wechat success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void shareFailed() {

                }
            })
            .setQqShareCallBack(new ShareCallBack() {
                @Override
                public void shareSuccess() {

                }

                @Override
                public void shareFailed() {
                    Toast.makeText(getApplicationContext(), "QQ FAILED", Toast.LENGTH_SHORT).show();
                }
            })
            .setShareTo(ShareConstant.WECHAT)
            .setShareTo(ShareConstant.WECHAT_FAVOURITE)
            .setShareTo(ShareConstant.WECHAT_TIMELINE)
            .setShareTo(ShareConstant.QQ)
            .startShare()
        ;

    }

}
